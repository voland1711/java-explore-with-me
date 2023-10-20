package ru.practicum.exception;

public class RequestRepeatException extends RuntimeException{
    public RequestRepeatException(String message) {
        super(message);
    }
}
