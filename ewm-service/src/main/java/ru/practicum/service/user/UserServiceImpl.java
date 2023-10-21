package ru.practicum.service.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.user.NewUserRequest;
import ru.practicum.dto.user.UserDto;
import ru.practicum.exception.EntityExistException;
import ru.practicum.exception.ObjectNotFoundException;
import ru.practicum.model.User;
import ru.practicum.model.mapper.UserMapper;
import ru.practicum.repository.UserRepository;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.model.mapper.UserMapper.toUser;
import static ru.practicum.model.mapper.UserMapper.toUserDto;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Transactional
    @Override
    public UserDto createUser(NewUserRequest newUserRequest) {
        log.info("Поступил запрос в UserServiceImpl.createUser на создание пользователя" +
                " newUserRequest = {}", newUserRequest);
        if (userRepository.findByName(newUserRequest.getName()) != null) {
            throw new EntityExistException("Имя " + newUserRequest.getName() + ", присутствует в коллекции");
        }
        User tempUser = toUser(newUserRequest);
        userRepository.save(tempUser);
        return toUserDto(tempUser);
    }

    @Override
    public List<UserDto> getUsers(List<Long> ids, PageRequest of) {
        log.info("Поступил запрос в UserServiceImpl.getUsers на получении списка пользователей" +
                " с параметрами: ids = {}, of = {}", ids, of);
        List<UserDto> userDtoList;
        if (ids == null || ids.isEmpty()) {
            userDtoList = toListUserDto(userRepository.getAll(of).getContent());
        } else {
            userDtoList = toListUserDto(userRepository.getAllByIdIn(ids, of));
        }
        return userDtoList.stream()
                .sorted(Comparator.comparingLong(UserDto::getId))
                .collect(Collectors.toList());
    }

    @Override
    public void deleteUserById(Long userId) {
        log.info("Поступил запрос в UserServiceImpl.deleteUserById на удаление пользователя" +
                " с параметром: userId = {}", userId);
        userRepository.findById(userId)
                .orElseThrow(() -> new ObjectNotFoundException("Пользователь с id: " + userId + " не найден"));
        userRepository.deleteById(userId);
    }

    private List<UserDto> toListUserDto(List<User> users) {
        return users.stream().map(UserMapper::toUserDto).collect(Collectors.toList());
    }
}
