package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingInputDto;
import ru.practicum.shareit.booking.dto.BookingOutputDto;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@Transactional
public class BookingServiceTests {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    private User user;
    private Item item;
    private BookingInputDto bookingInputDto;

    @BeforeEach
    void setUp() {
        // Очистка базы данных перед каждым тестом
        bookingRepository.deleteAll();
        itemRepository.deleteAll();
        userRepository.deleteAll();

        // Создание пользователя
        user = new User();
        user.setName("Test User");
        user.setEmail("test@example.com");
        user = userRepository.save(user);

        // Создание вещи
        item = new Item();
        item.setName("Test Item");
        item.setDescription("Test Description");
        item.setAvailable(true);
        item.setOwner(user.getId());
        item = itemRepository.save(item);

        // Создание DTO для бронирования
        bookingInputDto = new BookingInputDto();
        bookingInputDto.setStart(LocalDateTime.now().plusHours(1));
        bookingInputDto.setEnd(LocalDateTime.now().plusHours(2));
        bookingInputDto.setItemId(item.getId());
    }

    @Test
    void testCreateBooking() {
        BookingOutputDto bookingOutputDto = bookingService.create(bookingInputDto, user.getId());
        assertNotNull(bookingOutputDto);
        assertEquals(bookingInputDto.getStart(), bookingOutputDto.getStart());
        assertEquals(bookingInputDto.getEnd(), bookingOutputDto.getEnd());
        assertEquals(item.getId(), bookingOutputDto.getItem().getId());
        assertEquals(user.getId(), bookingOutputDto.getBooker().getId());
    }

    @Test
    void testGetBookingById() {
        BookingOutputDto bookingOutputDto = bookingService.create(bookingInputDto, user.getId());
        BookingOutputDto retrievedBooking = bookingService.getById(bookingOutputDto.getId(), user.getId());
        assertNotNull(retrievedBooking);
        assertEquals(bookingOutputDto.getId(), retrievedBooking.getId());
    }

    @Test
    void testGetAllUsersBookingByStatus() {
        bookingService.create(bookingInputDto, user.getId());
        List<BookingOutputDto> bookings = bookingService.getAllUsersBookingByStatus(user.getId(), StatusForBookingSearch.ALL);
        assertFalse(bookings.isEmpty());
        assertEquals(1, bookings.size());
    }

    @Test
    void testUpdateBookingStatus() {
        BookingOutputDto bookingOutputDto = bookingService.create(bookingInputDto, user.getId());
        BookingOutputDto updatedBooking = bookingService.updateBookingStatus(bookingOutputDto.getId(), user.getId(), true);
        assertEquals(Status.APPROVED, updatedBooking.getStatus());
    }

    @Test
    void testGetAllBookingForUserItemsByStatus() {
        bookingService.create(bookingInputDto, user.getId());
        List<BookingOutputDto> bookings = bookingService.getAllBookingForUserItemsByStatus(user.getId(), StatusForBookingSearch.ALL);
        assertFalse(bookings.isEmpty());
        assertEquals(1, bookings.size());
    }

    @Test
    void testGetByIdWithInvalidUserId() {
        BookingOutputDto bookingOutputDto = bookingService.create(bookingInputDto, user.getId());
        assertThrows(NotFoundException.class, () -> bookingService.getById(bookingOutputDto.getId(), 999L));
    }

    @Test
    void testCreateBookingWithUnavailableItem() {
        item.setAvailable(false);
        itemRepository.save(item);
        assertThrows(ConflictException.class, () -> bookingService.create(bookingInputDto, user.getId()));
    }

    @Test
    void testCreateBookingWithNonExistentUser() {
        assertThrows(NotFoundException.class, () -> bookingService.create(bookingInputDto, 999L));
    }
}
