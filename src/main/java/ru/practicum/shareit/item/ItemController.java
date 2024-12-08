package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.validation.CreateItemGroup;
import ru.practicum.shareit.validation.UpdateItemGroup;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/items")
@Validated
public class ItemController {

    private final ItemService itemService;

    @GetMapping
    public List<ItemDto> getAllByUserId(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Поступил запрос Get /items от пользователя с id = {} на получение всех его Item", userId);
        List<ItemDto> itemsDto = itemService.getAllByUserId(userId);
        log.info("Сформирован ответ Get /items с телом: {}", itemsDto);
        return itemsDto;
    }

    @GetMapping("/{id}")
    public ItemDto getById(@PathVariable Long id) {
        log.info("Поступил запрос Get /items/{} на получение Item с id = {}", id, id);
        ItemDto itemDto = itemService.getById(id);
        log.info("Сформирован ответ Get /items/{} с телом: {}", id, itemDto);
        return itemDto;
    }

    @GetMapping("/search")
    public List<ItemDto> getAllByParam(@RequestHeader("X-Sharer-User-Id") Long userId,
                                       @RequestParam String text) {
        log.info("Поступил запрос Get /items/search?text={} от пользователя с id = {} на получение Item с text {}", text, userId, text);
        List<ItemDto> itemsDto = itemService.findByText(text);
        log.info("Сформирован ответ Get /items/search?text={} с телом: {}", text, itemsDto);
        return itemsDto;
    }

    @PostMapping
    public ItemDto create(@RequestHeader("X-Sharer-User-Id") Long userId,
                          @Validated(CreateItemGroup.class) @RequestBody ItemDto itemDto) {
        log.info("Поступил запрос Post /items от пользователя с id = {} на добавление Item с телом {}", userId, itemDto);
        ItemDto newItemDto = itemService.create(userId, itemDto);
        log.info("Сформирован ответ Post /items с телом: {}", newItemDto);
        return newItemDto;
    }

    @PatchMapping("/{itemId}")
    public Item update(@RequestHeader("X-Sharer-User-Id") Long userId,
                       @Validated(UpdateItemGroup.class) @RequestBody Item item,
                       @PathVariable Long itemId) {
        log.info("Поступил запрос Patch /items/{} от пользователя с id = {} на изменение Item с id = {} с телом {}", itemId, userId, itemId, item);
        Item updatedItem = itemService.update(userId, itemId, item);
        log.info("Сформирован ответ Patch /items/{} с телом: {}", itemId, updatedItem);
        return updatedItem;
    }
}
