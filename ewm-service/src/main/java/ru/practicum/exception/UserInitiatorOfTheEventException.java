package ru.practicum.exception;

public class UserInitiatorOfTheEventException extends RuntimeException {
    public UserInitiatorOfTheEventException(String message) {
        super(message);
    }
}
