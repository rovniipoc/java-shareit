package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemService {
    private final ItemRepository itemRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;


    public List<ItemDto> getAllByUserId(Long userId) {
        return ItemMapper.toItemDto(itemRepository.findByOwner(userId));
    }

    public ItemDto getById(Long id) {
        Sort sort = Sort.by(Sort.Direction.DESC, "start");

        Item item = checkExistByItemId(id);
        List<Booking> bookings = bookingRepository.findByItemIdAndStatus(id, Status.APPROVED, sort);
        List<Comment> comments = commentRepository.findByItemId(id);
        return ItemMapper.toOwnerItemDto(item, bookings, comments);
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
        if (item.getOwner() == null) {
            item.setOwner(existingItem.getOwner());
        }
        if (item.getRequest() == null) {
            item.setRequest(existingItem.getRequest());
        }
        return itemRepository.save(item);
    }

    @Transactional
    public void deleteById(Long userId, Long itemId) {
        Item item = checkExistByItemId(itemId);
        checkUserOwner(userId, item);
        itemRepository.deleteById(itemId);
    }

    @Transactional
    public CommentDto createComment(Long userId, Long itemId, Comment comment) {
        User user = checkExistUserById(userId);
        Item item = checkExistByItemId(itemId);
        checkUserBookingItem(userId, itemId);
        comment.setAuthor(user);
        comment.setItem(item);
        return CommentMapper.toCommentDto(commentRepository.save(comment));
    }

    private Item checkExistByItemId(Long id) {
        return itemRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Вещь с id = " + id + " не существует"));
    }

    private User checkExistUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + id + " не существует"));
    }

    private void checkUserOwner(Long userId, Item item) {
        if (!userId.equals(item.getOwner())) {
            throw new ValidationException("Пользователь с id = " + userId + " не является владельцем вещи с id = " + item.getId());
        }
    }

    private void checkUserBookingItem(Long userId, Long itemId) {
        if (bookingRepository.findByBookerIdAndItemIdAndEndIsBefore(userId, itemId, LocalDateTime.now()).isEmpty()) {
            throw new ConflictException("У пользователя с id = " + userId + " нет завершенной аренды вещи с id = " + itemId);
        }
    }
}
