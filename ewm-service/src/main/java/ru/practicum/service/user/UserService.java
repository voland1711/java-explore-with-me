package ru.practicum.service.user;

import org.springframework.data.domain.PageRequest;
import ru.practicum.dto.user.NewUserRequest;
import ru.practicum.dto.user.UserDto;

import java.util.List;

public interface UserService {
    UserDto createUser(NewUserRequest newUserRequest);

    List<UserDto> getUsers(List<Long> ids, PageRequest of);

    void deleteUserById(Long userId);
}
