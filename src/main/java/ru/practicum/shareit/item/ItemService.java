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
import ru.practicum.shareit.item.dto.*;
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


    public List<ItemOutputDto> getAllByUserId(Long userId) {
        return ItemMapper.toItemOutputDto(itemRepository.findByOwner(userId));
    }

    public ItemOutputDto getById(Long id) {
        Sort sort = Sort.by(Sort.Direction.DESC, "start");

        Item item = checkExistByItemId(id);
        List<Booking> bookings = bookingRepository.findByItemIdAndStatus(id, Status.APPROVED, sort);
        List<Comment> comments = commentRepository.findByItemId(id);
        return ItemMapper.toDetailedItemOutputDto(item, bookings, comments);
    }

    // поиск Item с available = true по тексту, который содержится в name или description
    public List<ItemOutputDto> findByText(String text) {
        if (text.isBlank()) {
            return List.of();
        }
        return ItemMapper.toItemOutputDto(itemRepository.findByText(text));
    }

    @Transactional
    public ItemOutputDto create(Long userId, ItemInputDto itemInputDto) {
        Item item = ItemMapper.toItem(itemInputDto);
        checkExistUserById(userId);
        item.setOwner(userId);
        return ItemMapper.toItemOutputDto(itemRepository.save(item));
    }

    @Transactional
    public ItemOutputDto update(Long userId, Long itemId, ItemInputDto itemInputDto) {
        Item inputItem = ItemMapper.toItem(itemInputDto);
        Item existingItem = checkExistByItemId(itemId);
        checkExistUserById(userId);
        checkUserOwner(userId, existingItem);
        inputItem.setId(itemId);

        if (inputItem.getName() == null || inputItem.getName().isBlank()) {
            inputItem.setName(existingItem.getName());
        }
        if (inputItem.getDescription() == null || inputItem.getDescription().isBlank()) {
            inputItem.setDescription(existingItem.getDescription());
        }
        if (inputItem.getAvailable() == null) {
            inputItem.setAvailable(existingItem.getAvailable());
        }
        if (inputItem.getOwner() == null) {
            inputItem.setOwner(existingItem.getOwner());
        }
        if (inputItem.getRequest() == null) {
            inputItem.setRequest(existingItem.getRequest());
        }
        return ItemMapper.toItemOutputDto(itemRepository.save(inputItem));
    }

    @Transactional
    public void deleteById(Long userId, Long itemId) {
        Item item = checkExistByItemId(itemId);
        checkUserOwner(userId, item);
        itemRepository.deleteById(itemId);
    }

    @Transactional
    public CommentOutputDto createComment(Long userId, Long itemId, CommentInputDto commentInputDto) {
        User user = checkExistUserById(userId);
        Item item = checkExistByItemId(itemId);
        checkUserBookingItem(userId, itemId);
        Comment comment = CommentMapper.toComment(commentInputDto);
        comment.setAuthor(user);
        comment.setItem(item);
        return CommentMapper.toCommentOutputDto(commentRepository.save(comment));
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
