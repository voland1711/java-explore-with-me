package ru.practicum.stats;

import lombok.*;

@Getter
@Setter
@EqualsAndHashCode
@AllArgsConstructor
@ToString
public class HitResponseDto {
    private final String app;
    private final String uri;
    private final Long hits;
}
