package ru.practicum.stats;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode
@AllArgsConstructor
public class HitResponseDto {
    private final String app;
    private final String uri;
    private final Long hits;
}
