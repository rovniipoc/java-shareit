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

/**
 * TODO Sprint add-controllers.
 */

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/items")
@Validated
public class ItemController {

    private final ItemService itemService;

    @GetMapping
    public List<ItemDto> getAllByUserId(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Поступил запрос от пользователя с id = {} на получение всех его Item", userId);
        return itemService.getAllByUserId(userId);
    }

    @GetMapping("/{id}")
    public ItemDto getById(@PathVariable Long id) {
        log.info("Поступил запрос на получение Item с id = {}", id);
        return itemService.getById(id);
    }

    @GetMapping("/search")
    public List<ItemDto> getAllByParam(@RequestHeader("X-Sharer-User-Id") Long userId,
                                       @RequestParam String text) {
        log.info("Поступил запрос от пользователя с id = {} на получение Item с text {}", userId, text);
        return itemService.getAllAvailableByParam(text);
    }

    @PostMapping
    public ItemDto create(@RequestHeader("X-Sharer-User-Id") Long userId,
                          @Validated(CreateItemGroup.class) @RequestBody ItemDto itemDto) {
        log.info("Поступил запрос от пользователя с id = {} на добавление Item с телом {}", userId, itemDto);
        return itemService.create(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public Item update(@RequestHeader("X-Sharer-User-Id") Long userId,
                       @Validated(UpdateItemGroup.class) @RequestBody Item item,
                       @PathVariable Long itemId) {
        log.info("Поступил запрос от пользователя с id = {} на изменение Item с id = {} с телом {}", userId, itemId, item);
        return itemService.update(userId, itemId, item);
    }
}
