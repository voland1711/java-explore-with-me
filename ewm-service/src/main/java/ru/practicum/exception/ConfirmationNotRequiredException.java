package ru.practicum.exception;

public class ConfirmationNotRequiredException extends RuntimeException{
    public ConfirmationNotRequiredException(String message){
        super(message);
    }
}
