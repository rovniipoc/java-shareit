package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserInputDto;
import ru.practicum.shareit.user.dto.UserOutputDto;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @GetMapping
    public List<UserOutputDto> getAllUsers() {
        log.info("Поступил запрос Get /users на получение всех User");
        List<UserOutputDto> users = userService.getAll();
        log.info("Сформирован ответ Get /users с телом: {}", users);
        return users;
    }

    @GetMapping("/{id}")
    public UserOutputDto getUserById(@PathVariable Long id) {
        log.info("Поступил запрос Get /users/{} на получение User с id = {}", id, id);
        UserOutputDto userOutputDto = userService.getById(id);
        log.info("Сформирован ответ Get /users/{} с телом: {}", id, userOutputDto);
        return userOutputDto;
    }

    @PostMapping
    public UserOutputDto create(@RequestBody UserInputDto userInputDto) {
        log.info("Поступил запрос Post /users на создание User с телом {}", userInputDto);
        UserOutputDto newUserOutputDto = userService.create(userInputDto);
        log.info("Сформирован ответ Post /users с телом: {}", newUserOutputDto);
        return newUserOutputDto;
    }

    @PatchMapping("/{id}")
    public UserOutputDto update(@PathVariable Long id,
                                @RequestBody UserInputDto userInputDto) {
        log.info("Поступил запрос Patch /users/{} на обновление User с id = {} с телом {}", id, id, userInputDto);
        UserOutputDto updatedUserOutputDto = userService.update(id, userInputDto);
        log.info("Сформирован ответ Patch /users/{} с телом: {}", id, updatedUserOutputDto);
        return updatedUserOutputDto;
    }

    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable Long id) {
        log.info("Поступил запрос Delete /users/{} на удаление User с id = {}", id, id);
        userService.deleteById(id);
        log.info("Выполнен запрос Delete /users/{} на удаление User с id = {}", id, id);
    }
}
