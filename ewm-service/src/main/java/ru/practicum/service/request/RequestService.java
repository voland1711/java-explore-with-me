package ru.practicum.service.request;

import ru.practicum.dto.event.EventRequestStatusUpdateRequest;
import ru.practicum.dto.event.EventRequestStatusUpdateResult;
import ru.practicum.dto.participation.ParticipationRequestDto;

import java.util.List;

public interface RequestService {
    List<ParticipationRequestDto> getUserRequests(Long userId);

    ParticipationRequestDto createUserRequest(Long userId, Long eventId);

    ParticipationRequestDto cancelUserRequest(Long userId, Long requestId);

    List<ParticipationRequestDto> getEventByIdRequestsOwner(Long userId, Long eventId);

    EventRequestStatusUpdateResult updateEventByIdRequestsOwner(Long userId, Long eventId,
                                                                EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest);
}
