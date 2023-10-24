package ru.practicum.model.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.dto.event.EventRequestStatusUpdateResult;
import ru.practicum.dto.participation.ParticipationRequestDto;
import ru.practicum.model.Request;

import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RequestMapper {
    public static ParticipationRequestDto toParticipationRequestDto(Request request) {
        return ParticipationRequestDto.builder()
                .id(request.getId())
                .created(request.getCreated())
                .event(request.getEvent().getId())
                .requester(request.getRequester().getId())
                .status(request.getStatus())
                .build();
    }

    public static Request toRequest(ParticipationRequestDto participationRequestDto) {
        return Request.builder()
                .id(participationRequestDto.getId())
                .created(participationRequestDto.getCreated())
                .event(null)
                .requester(null)
                .status(participationRequestDto.getStatus())
                .build();
    }

    public static EventRequestStatusUpdateResult toParticipantRequestDto(List<Request> collectResult, List<Request> collectResult1) {
        return EventRequestStatusUpdateResult.builder()
                .confirmedRequests(collectResult.stream()
                        .map(RequestMapper::toParticipationRequestDto).collect(Collectors.toList()))
                .rejectedRequests(collectResult1.stream()
                        .map(RequestMapper::toParticipationRequestDto).collect(Collectors.toList())).build();
    }
}
