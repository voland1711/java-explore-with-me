package ru.practicum.model.mapper;

import lombok.NonNull;
import lombok.experimental.UtilityClass;
import ru.practicum.dto.comment.CommentFullDto;
import ru.practicum.dto.comment.CommentShortDto;
import ru.practicum.dto.comment.NewCommentDto;
import ru.practicum.model.Comment;

import static ru.practicum.model.mapper.EventMapper.toEventShortDto;
import static ru.practicum.model.mapper.UserMapper.toUserShortDto;

@UtilityClass
public class CommentMapper {
    public static CommentShortDto toCommentShortDto(@NonNull Comment comment) {
        return CommentShortDto
                .builder()
                .id(comment.getId())
                .commentText(comment.getCommentText())
                .createdOn(comment.getCreatedOn())
                .editedOn(comment.getEditedOn() != null ? comment.getEditedOn() : comment.getCreatedOn())
                .build();
    }

    public static CommentFullDto toCommentFullDto(@NonNull Comment comment) {
        return CommentFullDto
                .builder()
                .id(comment.getId())
                .commentText(comment.getCommentText())
                .createdOn(comment.getCreatedOn())
                .event(toEventShortDto(comment.getEvent()))
                .initiator(toUserShortDto(comment.getInitiator()))
                .editedOn(comment.getEditedOn() != null ? comment.getEditedOn() : comment.getCreatedOn())
                .build();
    }

    public static Comment toComment(@NonNull NewCommentDto newCommentDto) {
        return Comment
                .builder()
                .commentText(newCommentDto.getCommentText())
                .build();
    }
}
