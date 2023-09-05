package ru.practicum.stats.service;

import ru.practicum.stats.HitRequestDto;
import ru.practicum.stats.HitResponseDto;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsService {
    void saveHitDto(HitRequestDto hitRequestDto);

    List<HitResponseDto> getHits(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique);
}
