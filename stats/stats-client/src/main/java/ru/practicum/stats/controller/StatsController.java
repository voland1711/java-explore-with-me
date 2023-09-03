package ru.practicum.stats.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.practicum.stats.HitRequestDto;
import ru.practicum.stats.client.StatsClient;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping
public class StatsController {
    private final StatsClient statClient;

    @PostMapping(path = "/hit")
    public ResponseEntity<Object> save(@Valid @RequestBody HitRequestDto hitRequestDto) {
        log.info("Сохранение данных");
        statClient.saveHit(hitRequestDto);
        return new ResponseEntity<>("Информация сохранена", HttpStatus.CREATED);
    }

    @GetMapping(path = "/stats")
    public ResponseEntity<Object> getStat(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime start,
                                          @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime end,
                                          @RequestParam(defaultValue = "false") Boolean unique,
                                          @RequestParam(required = false) List<String> uris) {
        log.info("Получение данных");
        log.info("start = " + start);
        log.info("end = " + end);
        log.info("unique = " + unique);
        return statClient.getHits(start, end, uris, unique);
    }
}
