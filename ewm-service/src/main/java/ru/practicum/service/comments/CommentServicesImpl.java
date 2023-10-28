package ru.practicum.service.comments;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.comment.CommentFullDto;
import ru.practicum.dto.comment.CommentShortDto;
import ru.practicum.dto.comment.NewCommentDto;
import ru.practicum.dto.comment.UpdateCommentDto;
import ru.practicum.exception.AccessDeniedException;
import ru.practicum.exception.CommentRepeatException;
import ru.practicum.exception.ObjectNotFoundException;
import ru.practicum.model.Comment;
import ru.practicum.model.Event;
import ru.practicum.model.User;
import ru.practicum.model.mapper.CommentMapper;
import ru.practicum.repository.CommentRepository;
import ru.practicum.repository.EventRepository;
import ru.practicum.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.model.mapper.CommentMapper.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentServicesImpl implements CommentService {
    private final EventRepository eventRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;

    @Transactional
    @Override
    public CommentShortDto createComment(Long eventId, Long userId, NewCommentDto newCommentDto) {
        log.info("Поступил запрос в CommentServicesImpl.createComment на создание комментария" +
                " newCommentDto = {}, eventId = {}", newCommentDto, eventId);
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ObjectNotFoundException("Событие с id: " + eventId + " не найдено"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ObjectNotFoundException("Пользователь с id: " + userId + " не найден"));

        boolean existComment = commentRepository
                .findByCommentTextAndAndEventIdAndInitiatorId(newCommentDto.getCommentText(), eventId, userId)
                .isPresent();
        if (existComment) {
            throw new CommentRepeatException("Вы пытаетесь добавить повторный комментарий");
        }
        Comment tempComment = toComment(newCommentDto);
        tempComment.setCreatedOn(LocalDateTime.now());
        tempComment.setEvent(event);
        tempComment.setInitiator(user);
        commentRepository.save(tempComment);
        return toCommentShortDto(tempComment);
    }

    @Transactional
    @Override
    public CommentShortDto updateCommentByIdUser(Long userId, Long eventId, UpdateCommentDto updateCommentDto) {
        Long commentId = updateCommentDto.getId();
        commentRepository.findById(commentId)
                .orElseThrow(() -> new ObjectNotFoundException("Комментарий с id: " + commentId + " не найден"));
        Comment comment = commentRepository
                .findByIdAndInitiatorId(commentId, userId)
                .orElseThrow(() -> new AccessDeniedException("Ошибка обновления комментария: принадлежит другому пользователю"));
        eventRepository.findById(eventId)
                .orElseThrow(() -> new ObjectNotFoundException("Событие с id: " + eventId + " не найдено"));
        userRepository.findById(userId)
                .orElseThrow(() -> new ObjectNotFoundException("Пользователь с id: " + userId + " не найден"));
        comment.setCommentText(updateCommentDto.getCommentText());
        comment.setEditedOn(LocalDateTime.now());
        return toCommentShortDto(comment);
    }

    @Override
    public CommentFullDto getComment(Long commentId) {
        log.info("Поступил запрос в CommentServicesImpl.getComment с параметром commentId = {}", commentId);
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ObjectNotFoundException("Комментарий с id = " + commentId + " не найден"));
        return toCommentFullDto(comment);
    }

    @Override
    public List<CommentShortDto> getCommentAllByEvent(Long eventId, Integer from, Integer size) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ObjectNotFoundException("Событие с id: " + eventId + " не найдено"));
        return toListCommentShortDto(commentRepository.findAllByEvent(event, PageRequest.of(from / size, size)));
    }

    @Override
    public List<CommentShortDto> getCommentAllByInitiator(Long userId, Integer from, Integer size) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ObjectNotFoundException("Пользователь с id: " + userId + " не найден"));
        return toListCommentShortDto(commentRepository.findAllByInitiator(user, PageRequest.of(from / size, size)));
    }

    @Transactional
    @Override
    public void deleteCommentById(Long commentId) {
        commentRepository.findById(commentId)
                .orElseThrow(() -> new ObjectNotFoundException("Комментарий с id = " + commentId + " не найден "));
        commentRepository.deleteById(commentId);
        log.info("Удален комментарий, c commentId = {}", commentId);
    }

    private List<CommentShortDto> toListCommentShortDto(List<Comment> comments) {
        return comments.stream().map(CommentMapper::toCommentShortDto).collect(Collectors.toList());
    }
}
