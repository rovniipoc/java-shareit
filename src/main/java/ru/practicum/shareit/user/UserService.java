package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public List<UserDto> getAll() {
        return userRepository.getAll().stream().map(UserMapper::toUserDto).toList();
    }

    public UserDto getById(Long id) {
        checkExistByUserId(id);
        return UserMapper.toUserDto(userRepository.getById(id));
    }

    public User create(User user) {
        checkDuplicateUserByEmail(user);
        return userRepository.create(user);
    }

    public User update(Long id, User user) {
        checkExistByUserId(id);
        checkDuplicateUserByEmail(user);
        user.setId(id);
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(userRepository.getById(id).getName());
        }
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            user.setEmail(userRepository.getById(id).getEmail());
        }
        return userRepository.update(user);
    }

    public void deleteById(Long id) {
        checkExistByUserId(id);
        userRepository.deleteById(id);
    }

    private void checkExistByUserId(Long id) {
        if (userRepository.getById(id) == null) {
            throw new NotFoundException("Пользователь с id = " + id + " не существует");
        }
    }

    private void checkDuplicateUserByEmail(User user) {
        List<User> duplicateUsers = userRepository.getAll().stream()
                .filter(u -> u.getEmail().equals(user.getEmail()))
                .toList();
        if (!duplicateUsers.isEmpty()) {
            throw new ValidationException("Пользователь с email = " + user.getEmail() + " уже существует");
        }
    }
}
