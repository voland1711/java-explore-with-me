package ru.practicum.exception;

public class EventBeenCanceled extends RuntimeException {
    public EventBeenCanceled(String message) {
        super(message);
    }
}
