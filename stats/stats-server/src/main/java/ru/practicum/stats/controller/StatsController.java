package ru.practicum.stats.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.stats.HitRequestDto;
import ru.practicum.stats.HitResponseDto;
import ru.practicum.stats.service.StatsServiceImpl;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequestMapping
@RequiredArgsConstructor()
public class StatsController {
    private final StatsServiceImpl statsService;

    @PostMapping("/hit")
    @ResponseStatus(value = HttpStatus.CREATED)
    public void save(@Valid @RequestBody HitRequestDto hitRequestDto) {
        log.info("Поступил hit, с параметрами: app = {}, uri = {}, ip = {}, timestamp = {}",
                hitRequestDto.getApp(),
                hitRequestDto.getUri(),
                hitRequestDto.getIp(),
                hitRequestDto.getTimeStamp());
        statsService.saveHitDto(hitRequestDto);
    }

    @GetMapping("/stats")
    @ResponseStatus(value = HttpStatus.OK)
    public List<HitResponseDto> getStats(@RequestParam(name = "start") LocalDateTime start,
                                         @RequestParam(name = "end") LocalDateTime end,
                                         @RequestParam(name = "uris", defaultValue = "") List<String> uris,
                                         @RequestParam(name = "unique", defaultValue = "false") boolean unique) {
        log.info("Поступил запрос на получение статистики " +
                "параметр start = {}, " +
                "параметр end = {}, " +
                "параметр uris = {}, " +
                "парметр unique = {}", start, end, uris, unique);
        return statsService.getHits(start, end, uris, unique);
    }
}
