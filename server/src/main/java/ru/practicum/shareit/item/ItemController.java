package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentInputDto;
import ru.practicum.shareit.item.dto.CommentOutputDto;
import ru.practicum.shareit.item.dto.ItemInputDto;
import ru.practicum.shareit.item.dto.ItemOutputDto;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/items")
@Validated
public class ItemController {

    private final ItemService itemService;
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    @GetMapping
    public List<ItemOutputDto> getAllByUserId(@RequestHeader(USER_ID_HEADER) Long userId) {
        log.info("Поступил запрос Get /items от пользователя с id = {} на получение всех его Item", userId);
        List<ItemOutputDto> itemsDto = itemService.getAllByUserId(userId);
        log.info("Сформирован ответ Get /items с телом: {}", itemsDto);
        return itemsDto;
    }

    @GetMapping("/{id}")
    public ItemOutputDto getById(@PathVariable Long id) {
        log.info("Поступил запрос Get /items/{} на получение Item с id = {}", id, id);
        ItemOutputDto itemOutputDto = itemService.getById(id);
        log.info("Сформирован ответ Get /items/{} с телом: {}", id, itemOutputDto);
        return itemOutputDto;
    }

    @GetMapping("/search")
    public List<ItemOutputDto> getAllByParam(@RequestHeader(USER_ID_HEADER) Long userId,
                                             @RequestParam String text) {
        log.info("Поступил запрос Get /items/search?text={} от пользователя с id = {} на получение Item с text {}", text, userId, text);
        List<ItemOutputDto> itemsDto = itemService.findByText(text);
        log.info("Сформирован ответ Get /items/search?text={} с телом: {}", text, itemsDto);
        return itemsDto;
    }

    @PostMapping
    public ItemOutputDto create(@RequestHeader(USER_ID_HEADER) Long userId,
                                @RequestBody ItemInputDto itemInputDto) {
        log.info("Поступил запрос Post /items от пользователя с id = {} на добавление Item с телом {}", userId, itemInputDto);
        ItemOutputDto newItemOutputDto = itemService.create(userId, itemInputDto);
        log.info("Сформирован ответ Post /items с телом: {}", newItemOutputDto);
        return newItemOutputDto;
    }

    @PatchMapping("/{itemId}")
    public ItemOutputDto update(@RequestHeader(USER_ID_HEADER) Long userId,
                                @RequestBody ItemInputDto itemInputDto,
                                @PathVariable Long itemId) {
        log.info("Поступил запрос Patch /items/{} от пользователя с id = {} на изменение Item с id = {} с телом {}", itemId, userId, itemId, itemInputDto);
        ItemOutputDto updatedItem = itemService.update(userId, itemId, itemInputDto);
        log.info("Сформирован ответ Patch /items/{} с телом: {}", itemId, updatedItem);
        return updatedItem;
    }

    @PostMapping("/{itemId}/comment")
    public CommentOutputDto create(@RequestHeader(USER_ID_HEADER) Long userId,
                                   @RequestBody CommentInputDto commentInputDto,
                                   @PathVariable Long itemId) {
        log.info("Поступил запрос Post /items/{}/comment от пользователя с id = {} на создание Comment для Item с id = {} с телом {}", itemId, userId, itemId, commentInputDto);
        CommentOutputDto newCommentOutputDto = itemService.createComment(userId, itemId, commentInputDto);
        log.info("Сформирован ответ Post /items/{}/comment с телом: {}", itemId, newCommentOutputDto);
        return newCommentOutputDto;
    }
}
