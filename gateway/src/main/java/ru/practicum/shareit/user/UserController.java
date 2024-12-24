package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserInputDto;
import ru.practicum.shareit.validation.CreateGroup;
import ru.practicum.shareit.validation.UpdateGroup;


@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping(path = "/users")
@Validated
public class UserController {
    private final UserClient userClient;

    @GetMapping
    public ResponseEntity<Object> getAllUsers() {
        log.info("Поступил запрос Get /users на получение всех User");
        ResponseEntity<Object> response = userClient.getAll();
        log.info("Сформирован ответ Get /users с телом: {}", response);
        return response;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getUserById(@PathVariable Long id) {
        log.info("Поступил запрос Get /users/{} на получение User с id = {}", id, id);
        ResponseEntity<Object> response = userClient.getById(id);
        log.info("Сформирован ответ Get /users/{} с телом: {}", id, response);
        return response;
    }

    @PostMapping
    public ResponseEntity<Object> create(@Validated(CreateGroup.class) @RequestBody UserInputDto userInputDto) {
        log.info("Поступил запрос Post /users на создание User с телом {}", userInputDto);
        ResponseEntity<Object> response = userClient.create(userInputDto);
        log.info("Сформирован ответ Post /users с телом: {}", response);
        return response;
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> update(@PathVariable Long id,
                                @Validated(UpdateGroup.class) @RequestBody UserInputDto userInputDto) {
        log.info("Поступил запрос Patch /users/{} на обновление User с id = {} с телом {}", id, id, userInputDto);
        ResponseEntity<Object> response = userClient.update(id, userInputDto);
        log.info("Сформирован ответ Patch /users/{} с телом: {}", id, response);
        return response;
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteById(@PathVariable Long id) {
        log.info("Поступил запрос Delete /users/{} на удаление User с id = {}", id, id);
        ResponseEntity<Object> response = userClient.deleteById(id);
        log.info("Выполнен запрос Delete /users/{} на удаление User с id = {}", id, id);
        return response;
    }

}
