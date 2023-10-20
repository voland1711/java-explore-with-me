package ru.practicum.service.events;

import io.micrometer.core.instrument.util.StringUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.config.*;
import ru.practicum.dto.event.*;
import ru.practicum.exception.*;
import ru.practicum.model.*;
import ru.practicum.model.mapper.EventMapper;
import ru.practicum.repository.*;
import ru.practicum.stats.HitRequestDto;
import ru.practicum.stats.client.StatsClient;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static ru.practicum.model.mapper.EventMapper.*;
import static ru.practicum.model.mapper.LocationMapper.toLocation;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    private final LocationRepository locationRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final EventRepository eventRepository;
    private final StatsClient statClient;

    @Override
    public List<EventFullDto> getEventsByAdmin(List<Long> users,
                                               List<EventState> states,
                                               List<Long> categories,
                                               LocalDateTime rangeStart,
                                               LocalDateTime rangeEnd,
                                               Integer from,
                                               Integer size) {
        log.info("Поступил запрос в EventServiceImpl.getEventsByAdmin на получение списка событий с параметрами:" +
                        " users = {}, states = {}, categories = {}, rangeStart = {}, rangeEnd = {}, from = {}, size = {}",
                users, states, categories, rangeStart, rangeEnd, from, size);
        Pageable pageable = PageRequest.of(from, size);
        List<Event> eventDtoList = eventRepository.findAll(
                buildSpecificationQuerySearchAdmin(users, states, categories, rangeStart, rangeEnd), pageable);
        return toListEventFullDto(eventDtoList).stream().sorted(Comparator.comparingLong(EventFullDto::getId).reversed()).collect(Collectors.toList());
    }

    @Transactional
    @Override
    public EventFullDto updateEventByAdmin(Long eventId, UpdateEventAdminRequest updateEventAdminRequest) {
        log.info("Поступил запрос в EventServiceImpl.updateEventByAdmin на с параметрами: eventId = {}," +
                " UpdateEventAdminRequest = {}", eventId, updateEventAdminRequest);
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ObjectNotFoundException("Событие с id = " + eventId + " не найдено"));
        event.setId(eventId);
        validateEventStateAdminUpdate(event);
        isBeforeHours(event.getEventDate(), 1L);
        if (Objects.nonNull(updateEventAdminRequest.getEventDate())) {
            isBeforeLocalDataNow(updateEventAdminRequest.getEventDate());
        }
        updateEventByAdmin(event, updateEventAdminRequest);
        updateStateAction(event, updateEventAdminRequest.getStateAction());
        return toEventFullDto(event);
    }

    private static void isBeforeLocalDataNow(LocalDateTime eventDate) {
        if (eventDate.isBefore(LocalDateTime.now())) {
            throw new LocalDateTimeException("Указана неподходящая дата события");
        }
    }

    @Override
    public List<EventShortDto> getEvents(String text,
                                         List<Long> categories,
                                         Boolean paid,
                                         LocalDateTime rangeStart,
                                         LocalDateTime rangeEnd,
                                         Boolean onlyAvailable,
                                         EventSortType sort,
                                         Integer from,
                                         Integer size,
                                         HttpServletRequest request) {
        log.info("Поступил запрос в EventServiceImpl.getEvents на получение списка событий с параметрами: text={}," +
                        " categories = {}, paid = {}, rangeStart = {}, rangeEnd = {}, onlyAvailable = {}," +
                        " sort = {}, from = {}, size = {}, request = {}", text, categories, paid, rangeStart, rangeEnd,
                onlyAvailable, sort, from, size, request);
        List<EventShortDto> eventShortDtoList;
        Pageable pageable = PageRequest.of(from, size, getTypeSort(sort));

        if (Objects.nonNull(rangeStart) && Objects.nonNull(rangeEnd)) {
            if (rangeEnd.isBefore(rangeStart)) {
                throw new LocalDateTimeException("Неверно указан временной интервал");
            }
        }

        List<Event> events = eventRepository.findAll(
                buildSpecificationQuerySearchUsers(text, categories, paid, rangeStart, rangeEnd, onlyAvailable),
                pageable);
        eventShortDtoList = toListEventShortDto(events);
        statsSave(request);
        return eventShortDtoList;
    }


    @Override
    public EventFullDto getEvent(Long eventId, HttpServletRequest request) {
        log.info("Поступил запрос в EventServiceImpl.getEvent на получение списка событий" +
                " с параметрами: id = {}, HttpServletRequest = {}", eventId, request);
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ObjectNotFoundException("Событие с id = " + eventId + " не найдено"));
        if (!event.getState().equals(EventState.PUBLISHED)) {
            throw new ObjectNotFoundException("Событие с идентификатором id = " + eventId + " не опубликовано");
        }
        statsSave(request);
        log.info("Метод EventServiceImpl.getEvent: найден Event ={}", event);
        event.setViews(event.getViews() + 1L);
        return toEventFullDto(event);
    }

    @Override
    public List<EventShortDto> getAllUsersEvents(Long userId, Integer from, Integer size) {
        log.info("Поступил запрос в EventServiceImpl.getAllUsersEvents на получение событий, добавленных текущим" +
                " пользователем с параметрами: userId = {}, from = {}, size = {}", userId, from, size);
        userRepository.findById(userId)
                .orElseThrow(() -> new ObjectNotFoundException("Пользователь с id: " + userId + " не найден"));
        List<EventShortDto> eventShortDtoList = new ArrayList<>();
        for (Event event : eventRepository.findAllByInitiatorId(userId, PageRequest.of(from, size))) {
            eventShortDtoList.add(toEventShortDto(event));
        }
        return eventShortDtoList;
    }

    @Override
    public EventFullDto createEvent(Long userId, NewEventDto newEventDto) {
        log.info("Поступил запрос в EventServiceImpl.createEvent на добавления нового события" +
                " с параметрами: userId = {}, newEventDto = {}", userId, newEventDto);
        if (Objects.nonNull(newEventDto.getEventDate())) {
            isBeforeLocalDataNow(newEventDto.getEventDate());
        }
        Event event = toEvent(newEventDto);
        Category category = categoryRepository.findById(newEventDto.getCategory())
                .orElseThrow(() -> new ObjectNotFoundException("Категория с id: " + newEventDto.getCategory() + " не найдена"));
        event.setCategory(category);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ObjectNotFoundException("Пользователь с id: " + userId + " не найден"));
        event.setInitiator(user);
        event.setState(EventState.PENDING);
        event.setCreatedOn(LocalDateTime.now());
        event.setParticipantLimit(newEventDto.getParticipantLimit() == null ? 0 : newEventDto.getParticipantLimit());
        event.setViews(0L);
        Location location = locationRepository.save(toLocation(newEventDto.getLocation()));
        event.setPaid(newEventDto.getPaid() != null && newEventDto.getPaid());
        event.setRequestModeration(newEventDto.getRequestModeration() == null || newEventDto.getRequestModeration());
        event.setLocation(location);
        return toEventFullDto(eventRepository.save(event));
    }

    @Override
    public EventFullDto getEventById(Long userId, Long eventId) {
        log.info("Поступил запрос в EventServiceImpl.getEventById на получение полной информации о событии текущим" +
                " пользователем с параметрами: userId = {}, eventId = {}", userId, eventId);
        userRepository.findById(userId)
                .orElseThrow(() -> new ObjectNotFoundException("Пользователь с id: " + userId + " не найден"));
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ObjectNotFoundException("Событие с id: " + eventId + " не найдено"));
        if (!event.getInitiator().getId().equals(userId)) {
            throw new AccessDeniedException("Запрашиваемое событие вам не принадлежит");
        }
        return toEventFullDto(event);
    }

    @Override
    public EventFullDto updateEventByIdOwner(Long userId, Long eventId, UpdateEventUserRequest updateEventUserRequest) {
        log.info("Поступил запрос в EventServiceImpl.updateEventByIdOwner для изменения события текущим пользователем" +
                        " с параметрами: userId = {}, eventId = {}, updateEventUserRequest = {} ",
                userId, eventId, updateEventUserRequest);
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ObjectNotFoundException("Событие с id = " + eventId + " не найдено"));
        userRepository.findById(userId)
                .orElseThrow(() -> new ObjectNotFoundException("Пользователь с id: " + userId + " не найден"));
        event.setId(eventId);
        validateEventStateUserUpdate(event);
        isBeforeHours(event.getEventDate(), 2L);
        if (Objects.nonNull(updateEventUserRequest.getEventDate())) {
            isBeforeLocalDataNow(updateEventUserRequest.getEventDate());
        }
        updateEventByUser(event, updateEventUserRequest);
        updateStateActionUsers(event, updateEventUserRequest.getStateAction());
        return toEventFullDto(event);
    }

    public static Specification<Event> buildSpecificationQuerySearchAdmin(List<Long> users, List<EventState> states, List<Long> categories,
                                                                          LocalDateTime rangeStart, LocalDateTime rangeEnd) {
        return (root, query, builder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (users != null) {
                root.get("initiator").in(users);
            }
            if (states != null) {
                root.get("state").in(states);
            }
            if ((categories != null) && (!categories.isEmpty())) {
                root.get("category").in(categories);
            }
            if (rangeStart != null && rangeEnd != null) {
                predicates.add(builder.between(root.get("eventDate"), rangeStart, rangeEnd));
            }
            return builder.and(predicates.toArray(new Predicate[0]));
        };
    }


    private Specification<Event> buildSpecificationQuerySearchUsers(String text, List<Long> categories, Boolean paid,
                                                                    LocalDateTime rangeStart, LocalDateTime rangeEnd, Boolean onlyAvailable) {
        return (root, query, builder) -> {
            List<Predicate> predicates = new ArrayList<>();
            Subquery<Long> subQueryLong = query.subquery(Long.class);
            Root<Request> requestRoot = subQueryLong.from(Request.class);
            Join<Request, Event> eventsRequests = requestRoot.join("event");

            predicates.add(builder.equal(root.get("state"), EventState.PUBLISHED));

            if (StringUtils.isNotBlank(text)) {
                String searchText = text.toLowerCase();
                predicates.add(builder.or(builder.like(builder.lower(root.get("annotation")), "%" + searchText + "%"),
                        builder.like(builder.lower(root.get("description")), "%" + searchText + "%")));
            }

            if (categories != null) {
                root.get("category").in(categories);
            }
            if (paid != null) {
                root.get("paid").in(paid);
            }

            if (Objects.nonNull(rangeStart) && Objects.nonNull(rangeEnd)) {
                if (rangeEnd.isAfter(rangeEnd)) {
                    throw new LocalDateTimeException("Неверно указан временной интервал");
                }
                predicates.add(builder.between(root.get("eventDate"), rangeStart, rangeEnd));
            } else {
                LocalDateTime now = LocalDateTime.now();
                predicates.add(builder.greaterThan(root.get("eventDate"), now));
            }

            if (onlyAvailable != null && onlyAvailable) {
                predicates.add(builder.or(builder.equal(root.get("participantLimit"), 0),
                        builder.and(builder.notEqual(root.get("participantLimit"), 0),
                                builder.greaterThan(root.get("participantLimit"), subQueryLong.select(builder.count(requestRoot.get("id")))
                                        .where(builder.equal(eventsRequests.get("id"), requestRoot.get("event").get("id")))
                                        .where(builder.equal(requestRoot.get("status"), RequestStatus.CONFIRMED))))));
            }
            return builder.and(predicates.toArray(new Predicate[0]));
        };
    }

    private Sort getTypeSort(EventSortType sort) {
        Sort.Direction direction = Sort.Direction.DESC;
        String property;
        if (sort == null) {
            property = "id";
            return Sort.by(direction, property);
        }
        switch (sort) {
            case EVENT_DATE:
                property = "eventDate";
                break;
            case VIEWS:
                property = "views";
                break;
            default:
                property = "id";
        }
        return Sort.by(direction, property);
    }

    private void validateEventStateUserUpdate(Event event) {
        if (event.getState().equals(EventState.PUBLISHED)) {
            throw new EventBeenPublished("Событие уже опубликовано");
        }
    }

    private void validateEventStateAdminUpdate(Event event) {
        if (event.getState().equals(EventState.PUBLISHED)) {
            throw new EventBeenPublished("Событие уже опубликовано");
        } else if (event.getState().equals(EventState.CANCELED)) {
            throw new EventBeenCanceled("Событие уже закрыто");
        }
    }

    private void isBeforeHours(LocalDateTime eventDate, Long hours) {
        if (eventDate.isBefore(LocalDateTime.now().plusHours(hours))) {
            throw new EventDateIsBeforeOneHours("Время до начала события менее " + hours + " часа/ов");
        }
    }

    private void updateStateAction(Event event, AdminEventState eventState) {
        if (Objects.nonNull(eventState)) {
            switch (eventState) {
                case PUBLISH_EVENT:
                    event.setState(EventState.PUBLISHED);
                    break;
                case REJECT_EVENT:
                    event.setState(EventState.CANCELED);
                    break;
                default:
                    throw new UnsupportedTypeException("Поле StateAction содержит неподдерживаемый тип данных");
            }
        }
    }

    private void updateStateActionUsers(Event event, UserEventState eventState) {
        if (Objects.nonNull(eventState)) {
            switch (eventState) {
                case SEND_TO_REVIEW:
                    event.setState(EventState.PENDING);
                    break;
                case CANCEL_REVIEW:
                    event.setState(EventState.CANCELED);
                    break;
                default:
                    throw new UnsupportedTypeException("Поле StateAction содержит неподдерживаемый тип данных");
            }
        }
    }

    private void updateEventByAdmin(Event event, UpdateEventAdminRequest updateEventAdminRequest) {
        if (Objects.nonNull(updateEventAdminRequest.getDescription())) {
            event.setDescription(updateEventAdminRequest.getDescription());
        }
        if (Objects.nonNull(updateEventAdminRequest.getAnnotation())) {
            event.setAnnotation(updateEventAdminRequest.getAnnotation());
        }
        if (Objects.nonNull(updateEventAdminRequest.getParticipantLimit())) {
            event.setParticipantLimit(updateEventAdminRequest.getParticipantLimit());
        }
        if (Objects.nonNull(updateEventAdminRequest.getTitle())) {
            event.setTitle(updateEventAdminRequest.getTitle());
        }
        if (Objects.nonNull(updateEventAdminRequest.getEventDate())) {
            event.setEventDate(updateEventAdminRequest.getEventDate());
        }
        if (Objects.nonNull(updateEventAdminRequest.getLocation())) {
            Location location = toLocation(updateEventAdminRequest.getLocation());
            location = locationRepository.save(location);
            event.setLocation(location);
        }
        if (Objects.nonNull(updateEventAdminRequest.getPaid())) {
            event.setPaid(updateEventAdminRequest.getPaid());
        }
        if (Objects.nonNull(updateEventAdminRequest.getCategoryId())) {
            Long categoryId = updateEventAdminRequest.getCategoryId();
            Category category = categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new ObjectNotFoundException("Категория с id: " + categoryId + " не найдена"));
            event.setCategory(category);
        }
    }


    private void updateEventByUser(Event event, UpdateEventUserRequest updateEventUserRequest) {
        if (Objects.nonNull(updateEventUserRequest.getDescription())) {
            event.setDescription(updateEventUserRequest.getDescription());
        }
        if (Objects.nonNull(updateEventUserRequest.getAnnotation())) {
            event.setAnnotation(updateEventUserRequest.getAnnotation());
        }
        if (Objects.nonNull(updateEventUserRequest.getParticipantLimit())) {
            event.setParticipantLimit(updateEventUserRequest.getParticipantLimit());
        }
        if (Objects.nonNull(updateEventUserRequest.getTitle())) {
            event.setTitle(updateEventUserRequest.getTitle());
        }
        if (Objects.nonNull(updateEventUserRequest.getEventDate())) {
            event.setEventDate(updateEventUserRequest.getEventDate());
        }
        if (Objects.nonNull(updateEventUserRequest.getLocation())) {
            event.setLocation(toLocation(updateEventUserRequest.getLocation()));
        }
        if (Objects.nonNull(updateEventUserRequest.getPaid())) {
            event.setPaid(updateEventUserRequest.getPaid());
        }
        if (Objects.nonNull(updateEventUserRequest.getCategoryId())) {
            Long categoryId = updateEventUserRequest.getCategoryId();
            Category category = categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new ObjectNotFoundException("Категория с id: " + categoryId + " не найдена"));
            event.setCategory(category);
        }
    }


    private void statsSave(HttpServletRequest request) {
        statClient.saveHit(new HitRequestDto("ewm-service-main", request.getRequestURI(), request.getRemoteAddr(),
                LocalDateTime.now()));
    }

    private List<EventShortDto> toListEventShortDto(List<Event> events) {
        return events.stream().map(EventMapper::toEventShortDto).collect(Collectors.toList());
    }

    private List<EventFullDto> toListEventFullDto(List<Event> events) {
        return events.stream().map(EventMapper::toEventFullDto).collect(Collectors.toList());
    }


}
