package ru.practicum.exception;

public class EventBeenPublished extends RuntimeException {
    public EventBeenPublished(String message) {
        super(message);
    }
}
