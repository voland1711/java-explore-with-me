package ru.practicum.stats.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.stats.exception.LocalDateTimeException;
import ru.practicum.stats.HitRequestDto;
import ru.practicum.stats.HitResponseDto;
import ru.practicum.stats.model.Hit;
import ru.practicum.stats.model.HitMapper;
import ru.practicum.stats.storage.StatsRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.stats.model.HitMapper.toHit;

@Service
@RequiredArgsConstructor
public class StatsServiceImpl implements StatsService {
    private final StatsRepository statsRepository;

    @Override
    public void saveHitDto(HitRequestDto hitRequestDto) {
        Hit hit = toHit(hitRequestDto);
        statsRepository.save(hit);
    }

    @Override
    public List<HitResponseDto> getHits(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {
        if (start.isBefore(end)) {
            throw new LocalDateTimeException("Неверно указаны даты");
        }
        if (unique) {
            if (uris.isEmpty()) {
                return statsRepository.getDistinctStatsAll(start, end).stream()
                        .map(HitMapper::toHitResponseDto)
                        .collect(Collectors.toList());
            } else {
                return statsRepository.getDistinctStats(start, end, uris).stream()
                        .map(HitMapper::toHitResponseDto)
                        .collect(Collectors.toList());
            }
        } else {
            if (uris.isEmpty()) {
                return statsRepository.getStatsAll(start, end).stream()
                        .map(HitMapper::toHitResponseDto)
                        .collect(Collectors.toList());
            } else {
                return statsRepository.getStatsInUris(start, end, uris).stream()
                        .map(HitMapper::toHitResponseDto)
                        .collect(Collectors.toList());
            }
        }
    }
}
