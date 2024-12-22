package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentInputDto;
import ru.practicum.shareit.item.dto.ItemInputDto;
import ru.practicum.shareit.validation.CreateGroup;
import ru.practicum.shareit.validation.UpdateGroup;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/items")
@Validated
public class ItemController {

    private final ItemClient itemClient;
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    @GetMapping
    public ResponseEntity<Object> getAllByUserId(@RequestHeader(USER_ID_HEADER) Long userId) {
        log.info("Поступил запрос Get /items от пользователя с id = {} на получение всех его Item", userId);
        ResponseEntity<Object> response = itemClient.getAllByUserId(userId);
        log.info("Сформирован ответ Get /items с телом: {}", response);
        return response;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getById(@PathVariable Long id) {
        log.info("Поступил запрос Get /items/{} на получение Item с id = {}", id, id);
        ResponseEntity<Object> response = itemClient.getById(id);
        log.info("Сформирован ответ Get /items/{} с телом: {}", id, response);
        return response;
    }

    @GetMapping("/search")
    public ResponseEntity<Object> getAllByParam(@RequestHeader(USER_ID_HEADER) Long userId,
                                             @RequestParam String text) {
        log.info("Поступил запрос Get /items/search?text={} от пользователя с id = {} на получение Item с text {}", text, userId, text);
        ResponseEntity<Object> response = itemClient.findByText(userId, text);
        log.info("Сформирован ответ Get /items/search?text={} с телом: {}", text, response);
        return response;
    }

    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader(USER_ID_HEADER) Long userId,
                                @Validated(CreateGroup.class) @RequestBody ItemInputDto itemInputDto) {
        log.info("Поступил запрос Post /items от пользователя с id = {} на добавление Item с телом {}", userId, itemInputDto);
        ResponseEntity<Object> response = itemClient.create(userId, itemInputDto);
        log.info("Сформирован ответ Post /items с телом: {}", response);
        return response;
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> update(@RequestHeader(USER_ID_HEADER) Long userId,
                       @Validated(UpdateGroup.class) @RequestBody ItemInputDto itemInputDto,
                       @PathVariable Long itemId) {
        log.info("Поступил запрос Patch /items/{} от пользователя с id = {} на изменение Item с id = {} с телом {}", itemId, userId, itemId, itemInputDto);
        ResponseEntity<Object> response = itemClient.update(userId, itemId, itemInputDto);
        log.info("Сформирован ответ Patch /items/{} с телом: {}", itemId, response);
        return response;
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> create(@RequestHeader(USER_ID_HEADER) Long userId,
                                   @Validated(CreateGroup.class) @RequestBody CommentInputDto commentInputDto,
                                   @PathVariable Long itemId) {
        log.info("Поступил запрос Post /items/{}/comment от пользователя с id = {} на создание Comment для Item с id = {} с телом {}", itemId, userId, itemId, commentInputDto);
        ResponseEntity<Object> response = itemClient.createComment(userId, itemId, commentInputDto);
        log.info("Сформирован ответ Post /items/{}/comment с телом: {}", itemId, response);
        return response;
    }
}
