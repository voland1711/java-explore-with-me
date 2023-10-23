package ru.practicum.service.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.config.EventState;
import ru.practicum.config.RequestStatus;
import ru.practicum.dto.event.EventRequestStatusUpdateRequest;
import ru.practicum.dto.event.EventRequestStatusUpdateResult;
import ru.practicum.dto.participation.ParticipationRequestDto;
import ru.practicum.exception.*;
import ru.practicum.model.Event;
import ru.practicum.model.Request;
import ru.practicum.model.User;
import ru.practicum.model.mapper.RequestMapper;
import ru.practicum.repository.EventRepository;
import ru.practicum.repository.RequestRepository;
import ru.practicum.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static ru.practicum.model.mapper.RequestMapper.toParticipationRequestDto;

@Slf4j
@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {
    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    @Override
    public List<ParticipationRequestDto> getUserRequests(Long userId) {
        log.info("Поступил запрос в RequestServiceImpl.getUserRequests на получение информации о заявках пользователя" +
                " с параметрами: userId = {}", userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ObjectNotFoundException("Пользователь с id: " + userId + " не найден"));
        return toListParticipationRequestDto(requestRepository.findAllByRequester(user));
    }

    @Transactional
    @Override
    public ParticipationRequestDto createUserRequest(Long userId, Long eventId) {
        log.info("Поступил запрос в RequestServiceImpl.createUserRequest на участии в событии пользователя" +
                " с параметрами: userId = {}, eventId = {}", userId, eventId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ObjectNotFoundException("Пользователь с id: " + userId + " не найден"));
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ObjectNotFoundException("Событие с id: " + eventId + " не найдено"));
        if (Boolean.TRUE.equals(requestRepository.existsByEventIdAndRequesterId(eventId, userId))) {
            throw new RequestRepeatException("Осуществлен повторный запрос");
        }
        validRequestByEvent(event, user);
        Request request = new Request();
        request.setRequester(user);
        request.setEvent(event);
        setStatus(event, request);
        request.setCreated(LocalDateTime.now());
        return toParticipationRequestDto(requestRepository.save(request));
    }

    @Transactional
    @Override
    public ParticipationRequestDto cancelUserRequest(Long userId, Long requestId) {
        log.info("Поступил запрос в RequestServiceImpl.cancelUserRequest для отмены заявки на участие в событии" +
                " с параметрами: userId = {}, requestId = {}", userId, requestId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ObjectNotFoundException("Пользователь с id: " + userId + " не найден"));
        Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> new ObjectNotFoundException("Запрос на участивев событии с id = " + requestId + " не найден"));
        validRequestByUser(request, user);
        request.setStatus(RequestStatus.CANCELED);
        return toParticipationRequestDto(request);
    }

    @Override
    public List<ParticipationRequestDto> getEventByIdRequestsOwner(Long userId, Long eventId) {
        log.info("Поступил запрос в RequestServiceImpl.getEventRequestsByIdOwner для отмены заявки на участие в событии" +
                " с параметрами: userId = {}, eventId = {}", userId, eventId);
        userRepository.findById(userId)
                .orElseThrow(() -> new ObjectNotFoundException("Пользователь с id: " + userId + " не найден"));
        eventRepository.findById(eventId)
                .orElseThrow(() -> new ObjectNotFoundException("Событие с id: " + eventId + " не найдено"));
        return toListParticipationRequestDto(requestRepository.findAllByEventId(eventId));
    }

    @Override
    public EventRequestStatusUpdateResult updateEventByIdRequestsOwner(Long userId, Long eventId,
                                                                       EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest) {
        log.info("\nПоступил запрос в RequestServiceImpl.updateEventByIdRequestsOwner для изменения события" +
                        " с параметрами: userId = {}, eventId = {}, eventRequestStatusUpdateRequest {}",
                userId, eventId, eventRequestStatusUpdateRequest);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ObjectNotFoundException("Пользователь с id: " + userId + " не найден"));
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ObjectNotFoundException("Событие с id: " + eventId + " не найдено"));

        if (!event.getInitiator().equals(user)) {
            throw new UserInitiatorOfTheEventException("Инициатор события не может создать запрос на участие");
        }
        if (event.getParticipantLimit() == 0 && Boolean.FALSE.equals(event.getRequestModeration())) {
            throw new ConfirmationNotRequiredException("Подтверждение заявок не требуется");
        }

        RequestStatus status = eventRequestStatusUpdateRequest.getStatus();
        List<Request> updateRequests = getRequestsPending(eventRequestStatusUpdateRequest.getRequestIds());

        if (!updateRequests.isEmpty() && status == RequestStatus.CONFIRMED) {
            setRequestStatus(event, updateRequests, status);
        } else if (updateRequests.isEmpty()) {
            throw new EventBeenPublished("События со статусом PENDING не найдены");
        } else if (status == RequestStatus.REJECTED) {
            updateRequests.forEach(request -> request.setStatus((RequestStatus.REJECTED)));
        }
        requestRepository.saveAllAndFlush(updateRequests);
        List<Request> requests = requestRepository.findAllByEventIdAndStatus(event, Arrays.asList(RequestStatus.CONFIRMED, RequestStatus.REJECTED));
        return RequestMapper.toParticipantRequestDto(collectResult(requests, RequestStatus.CONFIRMED),
                collectResult(requests, RequestStatus.REJECTED));
    }

    private List<Request> collectResult(List<Request> requests, RequestStatus status) {
        return requests.isEmpty() ? Collections.emptyList() : requests.stream()
                .filter(request -> request.getStatus() == status)
                .collect(Collectors.toList());
    }

    private void setRequestStatus(Event event, List<Request> updateRequests, RequestStatus status) {
        Long countByEventIdAndStatus = requestRepository.countByEventIdAndStatus(event.getId(), status);
        long differenceParticipantsConfirmed = event.getParticipantLimit() - countByEventIdAndStatus;
        if (differenceParticipantsConfirmed > 0) {
            updateRequests.forEach(request -> request.setStatus(updateRequests.indexOf(request) < differenceParticipantsConfirmed
                    ? RequestStatus.CONFIRMED
                    : RequestStatus.REJECTED));
        } else {
            throw new ExceedParticipantLimit("Первышен лимит участников");
        }
    }

    private Request getRequest(Long requestId) {
        return requestRepository.findById(requestId)
                .orElseThrow(() -> new ObjectNotFoundException("Запрос с id: " + requestId + " не найден"));
    }

    private List<Request> getRequestsPending(List<Long> setRequestId) {
        return setRequestId.stream().map(this::getRequest)
                .filter(requestStatus -> requestStatus.getStatus() == RequestStatus.PENDING)
                .collect(Collectors.toList());
    }

    private List<ParticipationRequestDto> toListParticipationRequestDto(List<Request> requests) {
        return requests.stream().map(RequestMapper::toParticipationRequestDto).collect(Collectors.toList());
    }

    private void setStatus(Event event, Request request) {
        if (event.getParticipantLimit() == 0 ||
                ((event.getParticipantLimit() > event.getParticipants().size()) && (!event.getRequestModeration()))) {
            request.setStatus(RequestStatus.CONFIRMED);
        } else {
            request.setStatus(RequestStatus.PENDING);
        }
    }

    private void validRequestByEvent(Event event, User user) {
        if (event.getInitiator().equals(user)) {
            throw new UserInitiatorOfTheEventException("Инициатор события не может создать запрос на участие");
        }
        if (event.getState() != EventState.PUBLISHED) {
            throw new EventBeenPublished("Событие должно быть опубликовано");
        }
        if (event.getParticipantLimit() != 0 && (event.getParticipants().size() >= event.getParticipantLimit())) {
            throw new ExceedParticipantLimit("Достигнут лимит запросов на участие");
        }
    }

    private void validRequestByUser(Request request, User user) {
        if (!request.getRequester().equals(user)) {
            throw new AccessDeniedException("Вы не имеете доступ к данному запросу на участие в событии");
        }
    }
}
