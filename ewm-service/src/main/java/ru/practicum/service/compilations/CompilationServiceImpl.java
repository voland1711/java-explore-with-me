package ru.practicum.service.compilations;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.compilation.CompilationDto;
import ru.practicum.dto.compilation.NewCompilationDto;
import ru.practicum.dto.compilation.UpdateCompilationRequest;
import ru.practicum.exception.ObjectNotFoundException;
import ru.practicum.model.Compilation;
import ru.practicum.model.Event;
import ru.practicum.model.mapper.CompilationMapper;
import ru.practicum.repository.CompilationRepository;
import ru.practicum.repository.EventRepository;

import java.util.*;
import java.util.stream.Collectors;

import static ru.practicum.model.mapper.CompilationMapper.toCompilation;
import static ru.practicum.model.mapper.CompilationMapper.toCompilationDto;

@Slf4j
@Service
@RequiredArgsConstructor
public class CompilationServiceImpl implements CompilationService {
    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;

    @Override
    public List<CompilationDto> getCompilationsAll(Boolean pinned, PageRequest of) {
        log.info("Поступил запрос в CompilationServiceImpl.getCompilationsAll на получении подборки событий" +
                " с параметрами: pinned = {}, pageRequst = {}", pinned, of);
        List<Compilation> compilations;
        compilations = compilationRepository.findAllByPinned(Objects.requireNonNullElse(pinned, false), of);
        return toCompilationsDto(compilations);
    }

    @Override
    public CompilationDto getCompilationById(Long compId) {
        log.info("Поступил запрос в CompilationServiceImpl.getCompilationById на получении подборки событий по id" +
                " с параметром compId = {}", compId);
        return toCompilationDto(compilationRepository.findById(compId)
                .orElseThrow(() -> new ObjectNotFoundException("Подборка с id = " + compId + " не найдена ")));
    }

    @Transactional
    @Override
    public CompilationDto createCompilation(NewCompilationDto newCompilationDto) {
        log.info("Поступил запрос в CompilationServiceImpl.createCompilation на получении подборки событий " +
                " с параметром NewCompilationDto = {}", newCompilationDto);
        Compilation compilation = toCompilation(newCompilationDto);
        compilation.setPinned(newCompilationDto.getPinned() != null && newCompilationDto.getPinned());
        setEvents(newCompilationDto.getEvents(), compilation);
        compilationRepository.save(compilation);
        return toCompilationDto(compilation);
    }

    private void setEvents(Set<Long> eventIds, Compilation compilation) {
        if (eventIds != null && !eventIds.isEmpty()) {
            compilation.setEvents(eventIds
                    .stream()
                    .map(this::getEvent)
                    .collect(Collectors.toSet()));
        } else {
            compilation.setEvents(Collections.emptySet());
        }
    }

    @Transactional
    @Override
    public CompilationDto updateCompilation(Long compId, UpdateCompilationRequest updateCompilationRequest) {
        log.info("Поступил запрос в CompilationServiceImpl.updateCompilation на обновление " +
                " с параметрами compId = {}, UpdateCompilationRequest = {}", compId, updateCompilationRequest);
        Compilation compilation = compilationRepository.findById(compId)
                .orElseThrow(() -> new ObjectNotFoundException("Подборка с id = " + compId + " не найдена "));
        Optional.ofNullable(updateCompilationRequest.getTitle()).ifPresent(compilation::setTitle);
        Optional.ofNullable(updateCompilationRequest.getPinned()).ifPresent(compilation::setPinned);
        setEvents(updateCompilationRequest.getEvents(), compilation);
        compilationRepository.saveAndFlush(compilation);
        return toCompilationDto(compilation);
    }

    @Override
    public void deleteCompilationById(Long compId) {
        log.info("Поступил запрос в CompilationServiceImpl.deleteCompilationById на удаление подборски по id" +
                " с параметром compId = {}", compId);
        compilationRepository.deleteById(compId);
    }

    private Event getEvent(Long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new ObjectNotFoundException("Событие с id: " + eventId + " не найдено"));
    }

    private List<CompilationDto> toCompilationsDto(List<Compilation> compilations) {
        return compilations.stream().map(CompilationMapper::toCompilationDto)
                .collect(Collectors.toList());
    }

}
