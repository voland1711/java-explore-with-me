package ru.practicum.controller.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.service.comments.CommentService;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("admin/comments")
@Validated
public class AdminCommentsController {
    private final CommentService commentService;

    @DeleteMapping("/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCommentById(@PathVariable Long commentId) {
        log.info("Поступил запрос в AdminCategoriesController.deleteCommentById с catId = {}", commentId);
        commentService.deleteCommentById(commentId);
    }
}
