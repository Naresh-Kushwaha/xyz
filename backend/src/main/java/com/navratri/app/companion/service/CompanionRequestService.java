package com.navratri.app.companion.service;

import com.navratri.app.common.exceptions.ForbiddenException;
import com.navratri.app.common.exceptions.ResourceNotFoundException;
import com.navratri.app.companion.dto.CompanionRequestDto;
import com.navratri.app.companion.dto.CreateCompanionRequestDto;
import com.navratri.app.companion.entity.CompanionRequest;
import com.navratri.app.companion.entity.CompanionRequestStatus;
import com.navratri.app.companion.repository.CompanionRequestRepository;
import com.navratri.app.event.entity.Event;
import com.navratri.app.event.repository.EventRepository;
import com.navratri.app.user.entity.User;
import com.navratri.app.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CompanionRequestService {

    private final CompanionRequestRepository companionRequestRepository;
    private final EventRepository eventRepository;
    private final UserService userService;

    @Transactional
    public CompanionRequestDto create(Long userId, CreateCompanionRequestDto dto) {
        User user = userService.getById(userId);

        Event event = null;
        if (dto.eventId() != null) {
            event = eventRepository.findById(dto.eventId())
                    .orElseThrow(() -> new ResourceNotFoundException("Event not found"));
        }

        CompanionRequest request = CompanionRequest.builder()
                .user(user)
                .event(event)
                .activity(dto.activity())
                .plannedTime(dto.plannedTime())
                .approximateArea(dto.approximateArea())
                .companionsNeeded(dto.companionsNeeded() > 0 ? dto.companionsNeeded() : 1)
                .message(dto.message())
                .build();

        return CompanionRequestDto.from(companionRequestRepository.save(request));
    }

    @Transactional(readOnly = true)
    public List<CompanionRequestDto> myRequests(Long userId) {
        return companionRequestRepository.findByUser_IdOrderByCreatedAtDesc(userId)
                .stream().map(CompanionRequestDto::from).toList();
    }

    @Transactional
    public void cancel(Long userId, Long requestId) {
        CompanionRequest request = companionRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Companion request not found"));

        if (!request.getUser().getId().equals(userId)) {
            throw new ForbiddenException("You can only cancel your own requests");
        }

        request.setStatus(CompanionRequestStatus.CANCELLED);
        companionRequestRepository.save(request);
    }

    public CompanionRequest getEntity(Long requestId) {
        return companionRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Companion request not found"));
    }
}
