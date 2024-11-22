package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.validation.CreateUserGroup;
import ru.practicum.shareit.validation.UpdateUserGroup;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
@Validated
public class UserController {
    private final UserService userService;

    @GetMapping
    public List<UserDto> getAllUsers() {
        log.info("Поступил запрос Get /users на получение всех User");
        List<UserDto> users = userService.getAll();
        log.info("Сформирован ответ Get /users с телом: {}", users);
        return users;
    }

    @GetMapping("/{id}")
    public UserDto getUserById(@PathVariable Long id) {
        log.info("Поступил запрос Get /users/{} на получение User с id = {}", id, id);
        UserDto userDto = userService.getById(id);
        log.info("Сформирован ответ Get /users/{} с телом: {}", id, userDto);
        return userDto;
    }

    @PostMapping
    public User create(@Validated(CreateUserGroup.class) @RequestBody User user) {
        log.info("Поступил запрос Post /users на создание User с телом {}", user);
        User newUser = userService.create(user);
        log.info("Сформирован ответ Post /users с телом: {}", newUser);
        return newUser;
    }

    @PatchMapping("/{id}")
    public User update(@PathVariable Long id,
                       @Validated(UpdateUserGroup.class) @RequestBody User user) {
        log.info("Поступил запрос Patch /users/{} на обновление User с id = {} с телом {}", id, id, user);
        User updatedUser = userService.update(id, user);
        log.info("Сформирован ответ Patch /users/{} с телом: {}", id, updatedUser);
        return updatedUser;
    }

    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable Long id) {
        log.info("Поступил запрос Delete /users/{} на удаление User с id = {}", id, id);
        userService.deleteById(id);
        log.info("Выполнен запрос Delete /users/{} на удаление User с id = {}", id, id);
    }
}
