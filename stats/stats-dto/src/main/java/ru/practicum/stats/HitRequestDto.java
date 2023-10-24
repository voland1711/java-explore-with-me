package ru.practicum.stats;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import ru.practicum.utils.CommonConstants;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;



@Getter
@Builder(toBuilder = true)
@AllArgsConstructor
public class HitRequestDto {
    @NotBlank(message = "Отсуствует название приложения")
    private final String app;
    @NotBlank(message = "Отсуствует эндпоинт сервиса, на который осущствлялся запрос")
    private final String uri;
    @NotBlank(message = "Отсуствует ip-адрес, с которого осуществлялся запрос")
    private final String ip;
    @NotNull(message = "Отсуствует время и дата/формат должен соответствовать yyyy-MM-dd HH:mm:ss")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = CommonConstants.DATETIME_FORMAT_TYPE)
    @JsonProperty("timestamp")
    private final LocalDateTime timeStamp;
}
