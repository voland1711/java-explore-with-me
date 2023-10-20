package ru.practicum.dto.compilation;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.dto.event.EventShortDto;

import java.util.List;

@Getter
@Setter
@Builder
public class CompilationDto {
    @JsonProperty(required = true)
    private Long id;

    private List<EventShortDto> events;

    @JsonProperty(required = true)
    private Boolean pinned;

    @JsonProperty(required = true)
    private String title;

}
