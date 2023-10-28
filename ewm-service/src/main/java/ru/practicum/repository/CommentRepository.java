package ru.practicum.repository;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.model.Comment;
import ru.practicum.model.Event;
import ru.practicum.model.User;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findAllByEvent(Event event, PageRequest of);

    List<Comment> findAllByInitiator(User user, PageRequest of);

    Optional<Comment> findByCommentTextAndAndEventIdAndInitiatorId(String commentText, Long eventId, Long userId);

    Optional<Comment> findByIdAndInitiatorId(Long commentId, Long userId);
}
