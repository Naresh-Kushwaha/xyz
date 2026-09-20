package com.navratri.app.event.controller;

import com.navratri.app.activity.ActivityType;
import com.navratri.app.common.ApiResponse;
import com.navratri.app.common.PageResponse;
import com.navratri.app.event.dto.CreateEventRequest;
import com.navratri.app.event.dto.EventDto;
import com.navratri.app.event.dto.JoinEventRequest;
import com.navratri.app.event.service.EventService;
import com.navratri.app.security.UserPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    @GetMapping
    public ApiResponse<PageResponse<EventDto>> discover(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) ActivityType activity,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ApiResponse.ok(eventService.discover(q, activity, PageRequest.of(page, size)));
    }

    @GetMapping("/{id}")
    public ApiResponse<EventDto> getById(@PathVariable Long id) {
        return ApiResponse.ok(eventService.getById(id));
    }

    @PostMapping
    public ApiResponse<EventDto> create(@AuthenticationPrincipal UserPrincipal principal,
                                         @Valid @RequestBody CreateEventRequest request) {
        return ApiResponse.ok("Event created", eventService.createEvent(principal.getId(), request));
    }

    @PostMapping("/{id}/join")
    public ApiResponse<Void> join(@AuthenticationPrincipal UserPrincipal principal,
                                   @PathVariable Long id,
                                   @RequestBody JoinEventRequest request) {
        eventService.joinEvent(id, principal.getId(), request);
        return ApiResponse.message("You're in! See you there.");
    }
}
