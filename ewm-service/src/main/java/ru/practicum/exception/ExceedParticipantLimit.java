package ru.practicum.exception;

public class ExceedParticipantLimit extends RuntimeException {
    public ExceedParticipantLimit(String message) {
        super(message);
    }
}
