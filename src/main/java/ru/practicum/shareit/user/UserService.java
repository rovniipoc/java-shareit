package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {
    private final UserRepository userRepository;

    public List<UserDto> getAll() {
        List<User> users = userRepository.findAll();
        return UserMapper.toUserDto(users);
    }

    public UserDto getById(Long id) {
        User user = checkExistByUserId(id);
        return UserMapper.toUserDto(user);
    }

    @Transactional
    public UserDto create(User user) {
        checkDuplicateUserByEmail(user);
        return UserMapper.toUserDto(userRepository.save(user));
    }

    @Transactional
    public UserDto update(Long id, User user) {
        User existingUser = checkExistByUserId(id);
        user.setId(id);

        checkDuplicateUserByEmail(user);
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(existingUser.getName());
        }
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            user.setEmail(existingUser.getEmail());
        }

        return UserMapper.toUserDto(userRepository.save(user));
    }

    @Transactional
    public void deleteById(Long id) {
        checkExistByUserId(id);
        userRepository.deleteById(id);
    }

    private User checkExistByUserId(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + id + " не существует"));
    }

    private void checkDuplicateUserByEmail(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new ValidationException("Пользователь с email = " + user.getEmail() + " уже существует");
        }
    }
}
