package ru.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.config.CommonConstants;
import ru.practicum.config.EventSortType;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.EventShortDto;
import ru.practicum.service.events.EventService;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/events")
@Validated
public class EventController {
    private final EventService eventService;

    @GetMapping
    public List<EventShortDto> getEvents(@RequestParam(required = false) String text,
                                         @RequestParam(required = false) List<Long> categories,
                                         @RequestParam(required = false) Boolean paid,
                                         @DateTimeFormat(pattern = CommonConstants.DATETIME_FORMAT_TYPE)
                                         @RequestParam(required = false) LocalDateTime rangeStart,
                                         @DateTimeFormat(pattern = CommonConstants.DATETIME_FORMAT_TYPE)
                                         @RequestParam(required = false) LocalDateTime rangeEnd,
                                         @RequestParam(defaultValue = "false") Boolean onlyAvailable,
                                         @RequestParam(required = false) EventSortType sort,
                                         @RequestParam(defaultValue = "0") Integer from,
                                         @RequestParam(defaultValue = "10") Integer size,
                                         HttpServletRequest request) {
        log.info("Поступил запрос в EventController.getEvents с параметрами:" +
                "text = {}" +
                "categories = {}" +
                "paid = {}" +
                "rangeStart = {}" +
                "rangeEnd = {}" +
                "onlyAvailable = {}" +
                "sort = {}" +
                "from = {}" +
                "size = {}", text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size);
        return eventService.getEvents(text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size, request);
    }

    @GetMapping("/{id}")
    public EventFullDto getEvent(@PathVariable Long id, HttpServletRequest request) {
        log.info("Поступил запрос в EventController.getEvent: eventId = {}, request = {}", id, request);
        return eventService.getEvent(id, request);
    }
}
