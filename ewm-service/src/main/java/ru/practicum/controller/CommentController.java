package ru.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.comment.CommentFullDto;
import ru.practicum.dto.comment.CommentShortDto;
import ru.practicum.service.comments.CommentService;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/comments")
@Validated
public class CommentController {
    private final CommentService commentService;

    @GetMapping("/{commentId}")
    public CommentFullDto getCommentById(@PathVariable Long commentId) {
        log.info("Поступил запрос в CommentController.getCommentById с параметром commentId = {}", commentId);
        return commentService.getComment(commentId);
    }

    @GetMapping("/event/{eventId}")
    public List<CommentShortDto> getCommentAllByEvent(@PathVariable Long eventId,
                                                      @RequestParam(defaultValue = "0") Integer from,
                                                      @RequestParam(defaultValue = "10") Integer size) {
        log.info("Поступил запрос в CommentController.getCommentAllByEvent с параметром eventId = {}," +
                " from = {}, size = {}", eventId, from, size);
        return commentService.getCommentAllByEvent(eventId, from, size);
    }

    @GetMapping("/user/{userId}")
    public List<CommentShortDto> getCommentAllByInitiator(@PathVariable Long userId,
                                                          @RequestParam(defaultValue = "0") Integer from,
                                                          @RequestParam(defaultValue = "10") Integer size) {
        log.info("Поступил запрос в CommentController.getCommentAllByUser с параметром eventId = {}", userId);
        return commentService.getCommentAllByInitiator(userId, from, size);
    }
}
