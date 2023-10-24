package ru.practicum.exception;

public class EventDateIsBeforeOneHours extends RuntimeException {
    public EventDateIsBeforeOneHours(String message) {
        super(message);
    }
}
