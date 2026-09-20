package com.navratri.app.matching.service;

import com.navratri.app.chat.entity.Conversation;
import com.navratri.app.chat.repository.ConversationRepository;
import com.navratri.app.common.exceptions.BadRequestException;
import com.navratri.app.common.exceptions.ForbiddenException;
import com.navratri.app.common.exceptions.ResourceNotFoundException;
import com.navratri.app.companion.dto.CompanionRequestDto;
import com.navratri.app.companion.entity.CompanionRequest;
import com.navratri.app.companion.entity.CompanionRequestStatus;
import com.navratri.app.companion.repository.CompanionRequestRepository;
import com.navratri.app.matching.dto.MatchCandidateDto;
import com.navratri.app.matching.dto.MatchDto;
import com.navratri.app.matching.entity.Match;
import com.navratri.app.matching.entity.MatchStatus;
import com.navratri.app.matching.repository.MatchRepository;
import com.navratri.app.user.entity.User;
import com.navratri.app.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * Explainable, rule-based matching. Every point added to a candidate's score comes
 * with a plain-language reason so the frontend can show "why" instead of a black-box
 * percentage -- see section 18 of the product spec ("explainable matching system").
 * Only ever compares information the user explicitly provided (activity, area,
 * time, interests) -- never infers anything.
 */
@Service
@RequiredArgsConstructor
public class MatchingService {

    private static final Duration TIME_WINDOW = Duration.ofHours(2);

    private final CompanionRequestRepository companionRequestRepository;
    private final MatchRepository matchRepository;
    private final ConversationRepository conversationRepository;
    private final UserService userService;

    @Transactional(readOnly = true)
    public List<MatchCandidateDto> findCandidates(Long userId, Long myRequestId) {
        CompanionRequest mine = companionRequestRepository.findById(myRequestId)
                .orElseThrow(() -> new ResourceNotFoundException("Companion request not found"));

        if (!mine.getUser().getId().equals(userId)) {
            throw new ForbiddenException("You can only find matches for your own requests");
        }

        List<CompanionRequest> others = companionRequestRepository
                .findByStatusAndActivityAndUser_IdNot(CompanionRequestStatus.OPEN, mine.getActivity(), userId);

        return others.stream()
                .map(other -> score(mine, other))
                .filter(c -> c.score() > 0)
                .sorted(Comparator.comparingInt(MatchCandidateDto::score).reversed())
                .limit(20)
                .toList();
    }

    private MatchCandidateDto score(CompanionRequest mine, CompanionRequest other) {
        int score = 0;
        List<String> reasons = new ArrayList<>();

        boolean sameEvent = mine.getEvent() != null && other.getEvent() != null
                && mine.getEvent().getId().equals(other.getEvent().getId());

        if (sameEvent) {
            score += 40;
            reasons.add("You're both going to " + mine.getEvent().getName());
        } else {
            score += 20;
            reasons.add("Both interested in " + prettyActivity(mine.getActivity()));
        }

        if (withinWindow(mine, other)) {
            score += 15;
            reasons.add("Both available around the same time");
        }

        if (sameArea(mine.getApproximateArea(), other.getApproximateArea())) {
            score += 15;
            reasons.add("Both in the " + mine.getApproximateArea() + " area");
        }

        Set<String> mySet = mine.getUser().getProfile() != null ? mine.getUser().getProfile().getInterests() : Set.of();
        Set<String> theirSet = other.getUser().getProfile() != null ? other.getUser().getProfile().getInterests() : Set.of();
        long sharedInterests = mySet.stream()
                .filter(i -> theirSet.stream().anyMatch(t -> t.equalsIgnoreCase(i)))
                .count();
        if (sharedInterests > 0) {
            score += (int) Math.min(15, sharedInterests * 5);
            reasons.add("You share " + sharedInterests + " interest" + (sharedInterests > 1 ? "s" : ""));
        }

        if (mine.getCompanionsNeeded() > 0 && other.getCompanionsNeeded() > 0) {
            score += 5;
            reasons.add("Group sizes are compatible");
        }

        return new MatchCandidateDto(CompanionRequestDto.from(other), score, reasons);
    }

    private boolean withinWindow(CompanionRequest a, CompanionRequest b) {
        long diffMinutes = Math.abs(ChronoUnit.MINUTES.between(a.getPlannedTime(), b.getPlannedTime()));
        return diffMinutes <= TIME_WINDOW.toMinutes();
    }

    private boolean sameArea(String a, String b) {
        if (a == null || b == null) return false;
        String na = a.trim().toLowerCase(Locale.ROOT);
        String nb = b.trim().toLowerCase(Locale.ROOT);
        return na.equals(nb) || na.contains(nb) || nb.contains(na);
    }

    private String prettyActivity(com.navratri.app.activity.ActivityType activity) {
        return activity.name().charAt(0) + activity.name().substring(1).toLowerCase(Locale.ROOT);
    }

    /**
     * User A says "I'm interested in going with the person behind this request" --
     * creates a PENDING match that the recipient must accept before any chat opens up.
     */
    @Transactional
    public MatchDto sendInterest(Long requesterUserId, Long theirCompanionRequestId) {
        CompanionRequest theirRequest = companionRequestRepository.findById(theirCompanionRequestId)
                .orElseThrow(() -> new ResourceNotFoundException("Companion request not found"));

        if (theirRequest.getUser().getId().equals(requesterUserId)) {
            throw new BadRequestException("You can't send interest to your own request");
        }
        if (theirRequest.getStatus() != CompanionRequestStatus.OPEN) {
            throw new BadRequestException("This request is no longer open");
        }

        matchRepository.findByRequesterUser_IdAndRecipientRequest_Id(requesterUserId, theirCompanionRequestId)
                .ifPresent(existing -> {
                    throw new BadRequestException("You've already sent interest for this request");
                });

        User requester = userService.getById(requesterUserId);

        Match match = Match.builder()
                .requesterUser(requester)
                .recipientUser(theirRequest.getUser())
                .recipientRequest(theirRequest)
                .status(MatchStatus.PENDING)
                .build();

        match = matchRepository.save(match);
        return MatchDto.from(match, requesterUserId);
    }

    @Transactional(readOnly = true)
    public List<MatchDto> myPendingRequests(Long userId) {
        return matchRepository.findPendingForRecipient(userId).stream()
                .map(m -> MatchDto.from(m, userId))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<MatchDto> myMatches(Long userId) {
        return matchRepository.findAllForUserByStatus(userId, MatchStatus.ACCEPTED).stream()
                .map(m -> MatchDto.from(m, userId))
                .toList();
    }

    @Transactional
    public MatchDto respond(Long userId, Long matchId, MatchStatus decision) {
        if (decision != MatchStatus.ACCEPTED && decision != MatchStatus.DECLINED && decision != MatchStatus.BLOCKED) {
            throw new BadRequestException("Decision must be ACCEPTED, DECLINED, or BLOCKED");
        }

        Match match = matchRepository.findById(matchId)
                .orElseThrow(() -> new ResourceNotFoundException("Match not found"));

        if (!match.getRecipientUser().getId().equals(userId)) {
            throw new ForbiddenException("Only the recipient can respond to this request");
        }
        if (match.getStatus() != MatchStatus.PENDING) {
            throw new BadRequestException("This request has already been responded to");
        }

        match.setStatus(decision);

        if (decision == MatchStatus.ACCEPTED) {
            Conversation conversation = Conversation.builder()
                    .userA(match.getRequesterUser())
                    .userB(match.getRecipientUser())
                    .build();
            conversation = conversationRepository.save(conversation);
            match.setConversation(conversation);
        }

        match = matchRepository.save(match);
        return MatchDto.from(match, userId);
    }
}
