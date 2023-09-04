package ru.practicum.stats.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode
@AllArgsConstructor
public class HitResponse {
    private String app;
    private String uri;
    private Long hits;
}
