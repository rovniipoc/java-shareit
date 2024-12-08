package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserMapper;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final ItemService itemService;
    private final UserService userService;

    public List<BookingDto> getAll() {
        List<Booking> bookings = bookingRepository.findAll();
        return BookingMapper.toBookingDto(bookings);
    }

    public Booking getById(Long bookingId, Long userId) {
        Booking booking = checkExistByBookingId(bookingId);
        checkUserAccessForBooking(booking, userId);
        return booking;
    }

    public List<Booking> getByItemId(Long itemId) {
        return bookingRepository.findByItemId(itemId);
    }

    @Transactional
    public Booking create(BookingDto bookingDto, Long userId) {
        User user = checkExistUserById(userId);
        Item item = checkExistByItemId(bookingDto.getItemId());
        checkAvailableItemById(bookingDto.getItemId());

        Booking booking = BookingMapper.toBooking(bookingDto, user, item);

        return bookingRepository.save(booking);
    }

    @Transactional
    public Booking updateBookingStatus(Long bookingId, Long userId, Boolean approve) {
        Booking booking = checkExistByBookingId(bookingId);
        checkUserIsItemsOwner(userId, booking.getItem().getId());
        if (approve) {
            booking.setStatus(Status.APPROVED);
        } else {
            booking.setStatus(Status.REJECTED);
        }
        return booking;
    }

    private ItemDto checkAvailableItemById(Long itemId) {
        ItemDto itemDto = itemService.getById(itemId);
        if (!itemService.getById(itemId).getAvailable()) {
            throw new ConflictException("Вещь с id = " + itemId + " не доступна к бронированию");
        }
        return itemDto;
    }

    private void checkUserIsItemsOwner(Long userId, Long itemId) {
        if (!itemService.getById(itemId).getOwner().equals(userId)) {
            throw new ConflictException("Вещь с id = " + itemId + " не принадлежит вам");
        }
    }

    private Booking checkExistByBookingId(Long id) {
        return bookingRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Бронирование с id = " + id + " не существует"));
    }

    private void checkUserAccessForBooking(Booking booking, Long userId) {
        Long ownerId = itemService.getById(booking.getId()).getOwner();
        if (!booking.getBooker().equals(userId) && !ownerId.equals(userId)) {
            throw new ValidationException("Данные о бронировании с id = " + booking.getId() + " доступны только заказчику и владельцу");
        }
    }

    private User checkExistUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + id + " не существует"));
    }

    private Item checkExistByItemId(Long id) {
        return itemRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Вещь с id = " + id + " не существует"));
    }
}
