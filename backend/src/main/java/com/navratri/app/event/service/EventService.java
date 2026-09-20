package com.navratri.app.event.service;

import com.navratri.app.activity.ActivityType;
import com.navratri.app.common.PageResponse;
import com.navratri.app.common.exceptions.ResourceNotFoundException;
import com.navratri.app.event.dto.CreateEventRequest;
import com.navratri.app.event.dto.EventDto;
import com.navratri.app.event.dto.JoinEventRequest;
import com.navratri.app.event.entity.Event;
import com.navratri.app.event.entity.EventParticipant;
import com.navratri.app.event.repository.EventParticipantRepository;
import com.navratri.app.event.repository.EventRepository;
import com.navratri.app.user.entity.User;
import com.navratri.app.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;
    private final EventParticipantRepository participantRepository;
    private final UserService userService;

    @Transactional
    public EventDto createEvent(Long organizerId, CreateEventRequest request) {
        User organizer = userService.getById(organizerId);

        Event event = Event.builder()
                .name(request.name())
                .description(request.description())
                .startTime(request.startTime())
                .endTime(request.endTime())
                .approximateLocation(request.approximateLocation())
                .latitude(request.latitude())
                .longitude(request.longitude())
                .entryFee(request.entryFee())
                .organizer(organizer)
                .activities(request.activities())
                .build();

        event = eventRepository.save(event);
        return toDto(event);
    }

    @Transactional(readOnly = true)
    public PageResponse<EventDto> discover(String query, ActivityType activity, Pageable pageable) {
        // "" matches every name/description/location as a substring, so this doubles as
        // "no filter" -- see the note on EventRepository.search() for why we never pass
        // null for the text query (activity, being a strongly-typed enum, is fine as null).
        String textFilter = query != null ? query : "";
        Page<Event> page = eventRepository.search(textFilter, activity, Instant.now(), pageable);
        return PageResponse.from(page.map(this::toDto));
    }

    @Transactional(readOnly = true)
    public EventDto getById(Long eventId) {
        Event event = findEvent(eventId);
        return toDto(event);
    }

    @Transactional
    public void joinEvent(Long eventId, Long userId, JoinEventRequest request) {
        Event event = findEvent(eventId);
        User user = userService.getById(userId);

        EventParticipant participant = participantRepository.findByEvent_IdAndUser_Id(eventId, userId)
                .orElse(EventParticipant.builder().event(event).user(user).build());

        participant.setLookingForCompanions(request.lookingForCompanions());
        participant.setCompanionActivity(request.companionActivity());
        participantRepository.save(participant);
    }

    private Event findEvent(Long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found"));
    }

    private EventDto toDto(Event event) {
        long attending = participantRepository.countByEvent_Id(event.getId());
        long lookingForCompanions = participantRepository.countByEvent_IdAndLookingForCompanionsTrue(event.getId());
        return EventDto.from(event, attending, lookingForCompanions);
    }
}
