package ru.practicum.model.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.dto.compilation.CompilationDto;
import ru.practicum.dto.compilation.NewCompilationDto;
import ru.practicum.model.Compilation;

import java.util.Collections;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CompilationMapper {
    public static CompilationDto toCompilationDto(Compilation compilation) {
        return CompilationDto.builder().id(compilation.getId())
                .events(compilation.getEvents() != null ? compilation.getEvents().stream()
                        .map(EventMapper::toEventShortDto).collect(Collectors.toList())
                        : Collections.emptyList())
                .pinned(compilation.getPinned()).title(compilation.getTitle()).build();
    }

    public static Compilation toCompilation(NewCompilationDto newDto) {
        return Compilation.builder()
                .pinned(newDto.getPinned() != null && newDto.getPinned())
                .title(newDto.getTitle()).build();
    }
}
