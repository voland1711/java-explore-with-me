package ru.practicum.exception;

public class CommentRepeatException extends RuntimeException {
    public CommentRepeatException(String message) {
        super(message);
    }
}
