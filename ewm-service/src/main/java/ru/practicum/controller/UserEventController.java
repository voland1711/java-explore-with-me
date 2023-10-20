package ru.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.event.*;
import ru.practicum.dto.participation.ParticipationRequestDto;
import ru.practicum.service.events.EventService;
import ru.practicum.service.request.RequestService;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/users/{userId}/events")
@Validated
public class UserEventController {
    private final RequestService requestService;
    private final EventService eventService;

    @GetMapping()
    @ResponseStatus(HttpStatus.OK)
    public List<EventShortDto> getAllUsersEvents(@PathVariable Long userId,
                                                 @RequestParam(defaultValue = "0") Integer from,
                                                 @RequestParam(defaultValue = "10") Integer size) {
        log.info("Поступил запрос в UserEventController.getUsersEvents");
        return eventService.getAllUsersEvents(userId, from, size);
    }

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto createEvent(@PathVariable Long userId,
                                    @Valid @RequestBody NewEventDto newEventDto) {
        log.info("Поступил запрос в UserEventController.createEvent {}", newEventDto);
        return eventService.createEvent(userId, newEventDto);
    }

    @GetMapping("/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto getEventById(
            @PathVariable Long userId,
            @PathVariable Long eventId) {
        log.info("Поступил запрос в UserEventController.getEventById");
        return eventService.getEventById(userId, eventId);
    }

    @PatchMapping("/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto updateEventByIdOwner(
            @PathVariable Long userId,
            @PathVariable Long eventId,
            @Valid @RequestBody UpdateEventUserRequest updateEventUserRequest) {
        log.info("Поступил запрос в UserEventController.updateEventByIdOwner");
        return eventService.updateEventByIdOwner(userId, eventId, updateEventUserRequest);
    }

    @GetMapping("/{eventId}/requests")
    @ResponseStatus(HttpStatus.OK)
    public List<ParticipationRequestDto> getEventByIdRequestsOwner(
            @PathVariable Long userId,
            @PathVariable Long eventId) {
        log.info("Поступил запрос в UserEventController.getEventRequestsByIdOwner");
        return requestService.getEventByIdRequestsOwner(userId, eventId);
    }

    @PatchMapping("/{eventId}/requests")
    @ResponseStatus(HttpStatus.OK)
    public EventRequestStatusUpdateResult updateEventByIdRequestsOwner(
            @PathVariable Long userId, @PathVariable Long eventId,
            @RequestBody EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest) {
        return requestService.updateEventByIdRequestsOwner(userId, eventId, eventRequestStatusUpdateRequest);
    }

}
