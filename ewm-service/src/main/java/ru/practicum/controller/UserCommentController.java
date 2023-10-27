package ru.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.comment.CommentShortDto;
import ru.practicum.dto.comment.NewCommentDto;
import ru.practicum.dto.comment.UpdateCommentDto;
import ru.practicum.service.comments.CommentService;

import javax.validation.Valid;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/users/{userId}/events/{eventId}/comments")
@Validated
public class UserCommentController {
    private final CommentService commentService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public CommentShortDto createComment(@PathVariable Long eventId,
                                         @PathVariable Long userId,
                                         @Valid @RequestBody NewCommentDto newCommentDto) {
        log.info("Поступил запрос в AdminCommentController.createComment с параметром newCommentDto = {}", newCommentDto);
        return commentService.createComment(eventId, userId, newCommentDto);
    }


    @PatchMapping
    @ResponseStatus(HttpStatus.OK)
    public CommentShortDto updateCommentByIdUser(@PathVariable Long userId,
                                              @PathVariable Long eventId,
                                              @Valid @RequestBody UpdateCommentDto updateCommentDto) {
        log.info("Поступил запрос в UserEventController.updateCommentByIdUser: userId = {}, eventId = {}, " +
                "updateEventUserRequest = {}", userId, eventId, updateCommentDto);
        return commentService.updateCommentByIdUser(userId, eventId, updateCommentDto);
    }


}
