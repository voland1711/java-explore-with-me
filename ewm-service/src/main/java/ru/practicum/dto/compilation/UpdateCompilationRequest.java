package ru.practicum.dto.compilation;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Size;
import java.util.Set;

@Getter
@Setter
@Builder
public class UpdateCompilationRequest {
    Set<Long> events;

    Boolean pinned;

    @Size(min = 1, max = 50)
    String title;

}
