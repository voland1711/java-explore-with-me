package ru.practicum.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.ToString;
import org.springframework.http.HttpStatus;
import ru.practicum.config.CommonConstants;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@ToString
public class ApiError {
    @JsonIgnore
    private final List<String> errors;

    private final String message;

    private final String reason;

    private final HttpStatus status;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = CommonConstants.DATETIME_FORMAT_TYPE)
    private final LocalDateTime timestamp;

    public ApiError(Throwable e, HttpStatus status, String reason) {
        this.message = e.getMessage();
        this.reason = reason;
        this.status = status;
        this.timestamp = LocalDateTime.now();
        this.errors = Arrays
                .stream(e.getStackTrace())
                .map(StackTraceElement::toString)
                .collect(Collectors.toList());
    }
}
