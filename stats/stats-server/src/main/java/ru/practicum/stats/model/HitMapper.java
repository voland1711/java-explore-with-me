package ru.practicum.stats.model;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.stats.HitRequestDto;
import ru.practicum.stats.HitResponseDto;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class HitMapper {
    public static Hit toHit(HitRequestDto hitRequestDto) {
        return Hit.builder()
                .app(hitRequestDto.getApp())
                .ip(hitRequestDto.getIp())
                .uri(hitRequestDto.getUri())
                .timestamp(hitRequestDto.getTimeStamp())
                .build();
    }

    public static HitResponseDto toHitResponseDto(HitResponse hitResponse) {
        return new HitResponseDto(
                hitResponse.getApp(),
                hitResponse.getUri(),
                hitResponse.getHits()
        );
    }
}
