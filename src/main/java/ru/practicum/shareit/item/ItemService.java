package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemService {
    private final ItemRepository itemRepository;
    private final UserService userService;

    public List<ItemDto> getAllByUserId(Long userId) {
        return itemRepository.getAllByUserId(userId).stream().map(ItemMapper::toItemDto).toList();
    }

    public ItemDto getById(Long id) {
        checkExistByItemId(id);
        return ItemMapper.toItemDto(itemRepository.getById(id));
    }

    public List<ItemDto> getAllAvailableByParam(String text) {
        if (text.isBlank()) {
            return List.of();
        }
        return itemRepository.getAllAvailableByParam(text).stream().map(ItemMapper::toItemDto).toList();
    }

    public ItemDto create(Long userId, ItemDto itemDto) {
        Item item = ItemMapper.toItem(itemDto);
        checkExistUserById(userId);
        item.setOwner(userId);
        return ItemMapper.toItemDto(itemRepository.create(item));
    }

    public Item update(Long userId, Long itemId, Item item) {
        checkExistByItemId(itemId);
        checkExistUserById(userId);
        Item currentItem = itemRepository.getById(itemId);
        checkUserOwner(userId, currentItem);
        item.setId(itemId);

        if (item.getName() == null || item.getName().isBlank()) {
            item.setName(currentItem.getName());
        }
        if (item.getDescription() == null || item.getDescription().isBlank()) {
            item.setDescription(currentItem.getDescription());
        }
        if (item.getAvailable() == null) {
            item.setAvailable(currentItem.getAvailable());
        }
        return itemRepository.update(item);
    }

    public void deleteById(Long userId, Long itemId) {
        checkUserOwner(userId, itemRepository.getById(itemId));
        itemRepository.deleteById(itemId);
    }

    private void checkExistByItemId(Long id) {
        if (itemRepository.getById(id) == null) {
            throw new NotFoundException("Вещь с id = " + id + " не существует");
        }
    }

    private void checkExistUserById(Long id) {
        userService.getById(id);
    }

    private void checkUserOwner(Long userId, Item item) {
        if (!userId.equals(item.getOwner())) {
            throw new ValidationException("Пользователь с id = " + userId + " не является владельцем вещи с id = " + item.getId());
        }
    }
}
