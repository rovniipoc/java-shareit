package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserService;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemService {
    private final ItemRepository itemRepository;
    private final UserService userService;


    public List<ItemDto> getAllByUserId(Long userId) {
        return ItemMapper.toItemDto(itemRepository.findByOwner(userId));
    }

    public ItemDto getById(Long id) {
        Item item = checkExistByItemId(id);
        return ItemMapper.toItemDto(item);
    }

    // поиск Item с available = true по тексту, который содержится в name или description
    public List<ItemDto> findByText(String text) {
        if (text.isBlank()) {
            return List.of();
        }
        return ItemMapper.toItemDto(itemRepository.findByText(text));
    }

    @Transactional
    public ItemDto create(Long userId, ItemDto itemDto) {
        Item item = ItemMapper.toItem(itemDto);
        checkExistUserById(userId);
        item.setOwner(userId);
        return ItemMapper.toItemDto(itemRepository.save(item));
    }

    @Transactional
    public Item update(Long userId, Long itemId, Item item) {
        Item existingItem = checkExistByItemId(itemId);
        checkExistUserById(userId);
        checkUserOwner(userId, existingItem);
        item.setId(itemId);

        if (item.getName() == null || item.getName().isBlank()) {
            item.setName(existingItem.getName());
        }
        if (item.getDescription() == null || item.getDescription().isBlank()) {
            item.setDescription(existingItem.getDescription());
        }
        if (item.getAvailable() == null) {
            item.setAvailable(existingItem.getAvailable());
        }
        return itemRepository.save(item);
    }

    @Transactional
    public void deleteById(Long userId, Long itemId) {
        Item item = checkExistByItemId(itemId);
        checkUserOwner(userId, item);
        itemRepository.deleteById(itemId);
    }

    private Item checkExistByItemId(Long id) {
        return itemRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Вещь с id = " + id + " не существует"));
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
