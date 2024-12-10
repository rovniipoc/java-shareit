package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    public List<BookingDto> getAll() {
        List<Booking> bookings = bookingRepository.findAll();
        return BookingMapper.toBookingDto(bookings);
    }

    // получение списка всех бронирований текущего пользователя
    public List<Booking> getAllUsersBookingByStatus(Long userId, StatusForBookingSearch statusForBookingSearch) {
        Sort sort = Sort.by(Sort.Direction.DESC, "start");

        switch (statusForBookingSearch) {
            case ALL -> {
                return bookingRepository.findByBookerId(userId, sort);
            }
            case CURRENT -> {
                return bookingRepository.findByBookerIdAndStartIsBeforeAndEndIsAfter(userId, LocalDateTime.now(), LocalDateTime.now(), sort);
            }
            case PAST -> {
                return bookingRepository.findByBookerIdAndEndIsBefore(userId, LocalDateTime.now(), sort);
            }
            case FUTURE -> {
                return bookingRepository.findByBookerIdAndStartIsAfter(userId, LocalDateTime.now(), sort);
            }
            case WAITING -> {
                return bookingRepository.findByBookerIdAndStatus(userId, Status.WAITING, sort);
            }
            case REJECTED -> {
                return bookingRepository.findByBookerIdAndStatus(userId, Status.REJECTED, sort);
            }
            default -> {
                return List.of();
            }
        }
    }

    public Booking getById(Long bookingId, Long userId) {
        Booking booking = checkExistByBookingId(bookingId);
        checkUserAccessForBooking(booking, userId);
        return booking;
    }

    public List<Booking> getByItemId(Long itemId) {
        return bookingRepository.findByItemId(itemId);
    }

    // получение списка бронирований для всех вещей текущего пользователя
    public List<Booking> getAllBookingForUserItemsByStatus(Long userId, StatusForBookingSearch statusForBookingSearch) {
        checkUserHaveItems(userId);
        Sort sort = Sort.by(Sort.Direction.DESC, "start");

        switch (statusForBookingSearch) {
            case ALL -> {
                return bookingRepository.findByItemOwner(userId, sort);
            }
            case CURRENT -> {
                return bookingRepository.findByItemOwnerAndStartIsBeforeAndEndIsAfter(userId, LocalDateTime.now(), LocalDateTime.now(), sort);
            }
            case PAST -> {
                return bookingRepository.findByItemOwnerAndEndIsBefore(userId, LocalDateTime.now(), sort);
            }
            case FUTURE -> {
                return bookingRepository.findByItemOwnerAndStartIsAfter(userId, LocalDateTime.now(), sort);
            }
            case WAITING -> {
                return bookingRepository.findByItemOwnerAndStatus(userId, Status.WAITING, sort);
            }
            case REJECTED -> {
                return bookingRepository.findByItemOwnerAndStatus(userId, Status.REJECTED, sort);
            }
            default -> {
                return List.of();
            }
        }
    }

    @Transactional
    public Booking create(BookingDto bookingDto, Long userId) {
        User user = checkExistUserById(userId);
        Item item = checkAvailableItemById(bookingDto.getItemId());

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

    private Item checkAvailableItemById(Long itemId) {
        Item item = checkExistByItemId(itemId);
        if (!item.getAvailable()) {
            throw new ConflictException("Вещь с id = " + itemId + " не доступна к бронированию");
        }
        return item;
    }

    private void checkUserIsItemsOwner(Long userId, Long itemId) {
        if (!checkExistByItemId(itemId).getOwner().equals(userId)) {
            throw new ConflictException("Вещь с id = " + itemId + " не принадлежит вам");
        }
    }

    private Booking checkExistByBookingId(Long id) {
        return bookingRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Бронирование с id = " + id + " не существует"));
    }

    private void checkUserAccessForBooking(Booking booking, Long userId) {
        Long ownerId = checkExistByItemId(booking.getItem().getId()).getOwner();
        if (!booking.getBooker().getId().equals(userId) && !ownerId.equals(userId)) {
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

    private void checkUserHaveItems(Long userId) {
        if (itemRepository.findByOwner(userId).isEmpty()) {
            throw new NotFoundException("У пользователя с id = " + userId + " нет вещей");
        }
    }
}
