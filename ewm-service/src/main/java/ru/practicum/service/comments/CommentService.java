package ru.practicum.service.comments;

import ru.practicum.dto.comment.CommentFullDto;
import ru.practicum.dto.comment.CommentShortDto;
import ru.practicum.dto.comment.NewCommentDto;
import ru.practicum.dto.comment.UpdateCommentDto;

import java.util.List;

public interface CommentService {
    CommentShortDto createComment(Long eventId, Long userId, NewCommentDto newCommentDto);

    CommentShortDto updateCommentByIdUser(Long userId, Long eventId, UpdateCommentDto updateCommentDto);

    CommentFullDto getComment(Long commentId);

    List<CommentShortDto> getCommentAllByEvent(Long eventId, Integer from, Integer size);

    List<CommentShortDto> getCommentAllByInitiator(Long userId, Integer from, Integer size);

    void deleteCommentById(Long commentId);
}
