package ru.practicum.stats.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.stats.config.BaseClient;
import ru.practicum.stats.HitRequestDto;
import ru.practicum.utils.CommonConstants;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class StatsClient extends BaseClient {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(CommonConstants.DATETIME_FORMAT_TYPE);

    @Autowired
    public StatsClient(@Value("${stats-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl))
                .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                .build()
        );
        log.info("Сервер хранения статистики располагается по адресу: {}", serverUrl);
    }

    public void saveHit(HitRequestDto hitRequestDto) {
        post("/hit", hitRequestDto);
    }

    public ResponseEntity<Object> getHits(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("start", start.format(formatter));
        parameters.put("end", end.format(formatter));
        parameters.put("unique", unique == null ? "" : unique);
        parameters.put("uris", uris == null ? "" : String.join(",", uris));
        return get("/stats?start={start}&end={end}&uris={uris}&unique={unique}", parameters);
    }
}
