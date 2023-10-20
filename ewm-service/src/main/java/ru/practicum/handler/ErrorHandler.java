package ru.practicum.handler;

import lombok.extern.slf4j.Slf4j;
import org.h2.jdbc.JdbcSQLIntegrityConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import ru.practicum.exception.*;
import ru.practicum.model.ApiError;

@RestControllerAdvice
@Slf4j
public class ErrorHandler {

    @ExceptionHandler({
            MethodArgumentNotValidException.class,
            MethodArgumentTypeMismatchException.class,
            UnsupportedTypeException.class,
            LocalDateTimeException.class
    })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Object> methodArgumentNotValidException(Exception e) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        ApiError responseBody = new ApiError(e, status, "Incorrectly made request.");
        log.error(String.valueOf(e));
        return new ResponseEntity<>(responseBody, status);
    }

    @ExceptionHandler({
            ObjectNotFoundException.class
    })
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<Object> objectNotFoundException(Exception e) {
        HttpStatus status = HttpStatus.NOT_FOUND;
        ApiError responseBody = new ApiError(e, status, "Not found");
        log.error(String.valueOf(e));
        return new ResponseEntity<>(responseBody, status);
    }

    @ExceptionHandler({
            EventBeenPublished.class,
            EntityExistException.class,
            UserInitiatorOfTheEventException.class,
            RequestRepeatException.class,
            JdbcSQLIntegrityConstraintViolationException.class,
            EventBeenCanceled.class,
            ExceedParticipantLimit.class,

            AccessDeniedException.class
    })
    @ResponseStatus(HttpStatus.CONFLICT)
    public ResponseEntity<Object> conflictException(Exception e) {
        HttpStatus status = HttpStatus.CONFLICT;
        ApiError responseBody = new ApiError(e, status, "Conflict");
        return new ResponseEntity<>(responseBody, status);
    }

}
