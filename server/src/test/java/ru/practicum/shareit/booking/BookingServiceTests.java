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
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserMapper;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
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
    private BookingOutputDto bookingOutputDto;
    private Long userId;
    private Long itemId;
    private Long bookingId;
    @Autowired
    private UserService userService;

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
        userId = user.getId();

        // Создание вещи
        item = new Item();
        item.setName("Test Item");
        item.setDescription("Test Description");
        item.setAvailable(true);
        item.setOwner(user.getId());
        item = itemRepository.save(item);
        itemId = item.getId();

        // Создание DTO для бронирования
        bookingInputDto = new BookingInputDto();
        bookingInputDto.setStart(LocalDateTime.now().plusHours(1));
        bookingInputDto.setEnd(LocalDateTime.now().plusHours(2));
        bookingInputDto.setItemId(itemId);

        // Создание DTO для бронирования
        bookingOutputDto = BookingOutputDto.builder().build();
        bookingOutputDto.setId(1L);
        bookingOutputDto.setStart(bookingInputDto.getStart());
        bookingOutputDto.setEnd(bookingInputDto.getEnd());
        bookingOutputDto.setItem(ItemMapper.toItemOutputDto(item));
        bookingOutputDto.setBooker(UserMapper.toUserOutputDto(user));
        bookingOutputDto.setStatus(Status.WAITING);

        bookingId = bookingOutputDto.getId();
    }

    @Test
    void testCreateBooking() {
        BookingOutputDto createdBooking = bookingService.create(bookingInputDto, userId);
        assertNotNull(createdBooking);
        assertEquals(bookingInputDto.getStart(), createdBooking.getStart());
        assertEquals(bookingInputDto.getEnd(), createdBooking.getEnd());
        assertEquals(itemId, createdBooking.getItem().getId());
        assertEquals(userId, createdBooking.getBooker().getId());
    }

    @Test
    void testGetBookingById() {
        BookingOutputDto createdBooking = bookingService.create(bookingInputDto, userId);
        BookingOutputDto retrievedBooking = bookingService.getById(createdBooking.getId(), userId);
        assertNotNull(retrievedBooking);
        assertEquals(createdBooking.getId(), retrievedBooking.getId());
    }

    @Test
    void testGetAllUsersBookingByStatusAll() {
        User user2 = new User();
        user2.setName("Test User 2");
        user2.setEmail("test2@example.com");
        user2 = userRepository.save(user2);
        Long user2Id = user2.getId();

        BookingInputDto pastBooking = new BookingInputDto();
        pastBooking.setBooker(userId);
        pastBooking.setItemId(itemId);
        pastBooking.setStart(LocalDateTime.now().minusDays(2));
        pastBooking.setEnd(LocalDateTime.now().minusDays(1));
        BookingOutputDto pastBookingDto = bookingService.create(pastBooking, user2Id);
        Long pastBookingId = pastBookingDto.getId();

        BookingInputDto currentBooking = new BookingInputDto();
        currentBooking.setBooker(userId);
        currentBooking.setItemId(itemId);
        currentBooking.setStart(LocalDateTime.now().minusDays(1));
        currentBooking.setEnd(LocalDateTime.now().plusDays(1));
        BookingOutputDto currentBookingDto = bookingService.create(currentBooking, user2Id);
        Long currentBookingId = currentBookingDto.getId();

        BookingInputDto futureBooking = new BookingInputDto();
        futureBooking.setBooker(userId);
        futureBooking.setItemId(itemId);
        futureBooking.setStart(LocalDateTime.now().plusDays(1));
        futureBooking.setEnd(LocalDateTime.now().plusDays(2));
        BookingOutputDto futureBookingDto = bookingService.create(futureBooking, user2Id);
        Long futureBookingId = futureBookingDto.getId();

        List<BookingOutputDto> bookings = bookingService.getAllUsersBookingByStatus(user2Id, StatusForBookingSearch.ALL);
        assertFalse(bookings.isEmpty());
        assertEquals(3, bookings.size());
    }

    @Test
    void testGetAllUsersBookingByStatusCurrent() {
        User user2 = new User();
        user2.setName("Test User 2");
        user2.setEmail("test2@example.com");
        user2 = userRepository.save(user2);
        Long user2Id = user2.getId();

        BookingInputDto pastBooking = new BookingInputDto();
        pastBooking.setBooker(userId);
        pastBooking.setItemId(itemId);
        pastBooking.setStart(LocalDateTime.now().minusDays(2));
        pastBooking.setEnd(LocalDateTime.now().minusDays(1));
        BookingOutputDto pastBookingDto = bookingService.create(pastBooking, user2Id);
        Long pastBookingId = pastBookingDto.getId();

        BookingInputDto currentBooking = new BookingInputDto();
        currentBooking.setBooker(userId);
        currentBooking.setItemId(itemId);
        currentBooking.setStart(LocalDateTime.now().minusDays(1));
        currentBooking.setEnd(LocalDateTime.now().plusDays(1));
        BookingOutputDto currentBookingDto = bookingService.create(currentBooking, user2Id);
        Long currentBookingId = currentBookingDto.getId();

        BookingInputDto futureBooking = new BookingInputDto();
        futureBooking.setBooker(userId);
        futureBooking.setItemId(itemId);
        futureBooking.setStart(LocalDateTime.now().plusDays(1));
        futureBooking.setEnd(LocalDateTime.now().plusDays(2));
        BookingOutputDto futureBookingDto = bookingService.create(futureBooking, user2Id);
        Long futureBookingId = futureBookingDto.getId();

        List<BookingOutputDto> bookings = bookingService.getAllUsersBookingByStatus(user2Id, StatusForBookingSearch.CURRENT);
        assertFalse(bookings.isEmpty());
        assertEquals(1, bookings.size());
        assertEquals(currentBookingId, bookings.getFirst().getId());
    }

    @Test
    void testGetAllUsersBookingByStatusPast() {
        User user2 = new User();
        user2.setName("Test User 2");
        user2.setEmail("test2@example.com");
        user2 = userRepository.save(user2);
        Long user2Id = user2.getId();

        BookingInputDto pastBooking = new BookingInputDto();
        pastBooking.setBooker(userId);
        pastBooking.setItemId(itemId);
        pastBooking.setStart(LocalDateTime.now().minusDays(2));
        pastBooking.setEnd(LocalDateTime.now().minusDays(1));
        BookingOutputDto pastBookingDto = bookingService.create(pastBooking, user2Id);
        Long pastBookingId = pastBookingDto.getId();

        BookingInputDto currentBooking = new BookingInputDto();
        currentBooking.setBooker(userId);
        currentBooking.setItemId(itemId);
        currentBooking.setStart(LocalDateTime.now().minusDays(1));
        currentBooking.setEnd(LocalDateTime.now().plusDays(1));
        BookingOutputDto currentBookingDto = bookingService.create(currentBooking, user2Id);
        Long currentBookingId = currentBookingDto.getId();

        BookingInputDto futureBooking = new BookingInputDto();
        futureBooking.setBooker(userId);
        futureBooking.setItemId(itemId);
        futureBooking.setStart(LocalDateTime.now().plusDays(1));
        futureBooking.setEnd(LocalDateTime.now().plusDays(2));
        BookingOutputDto futureBookingDto = bookingService.create(futureBooking, user2Id);
        Long futureBookingId = futureBookingDto.getId();

        List<BookingOutputDto> bookings = bookingService.getAllUsersBookingByStatus(user2Id, StatusForBookingSearch.PAST);
        assertFalse(bookings.isEmpty());
        assertEquals(1, bookings.size());
        assertEquals(pastBookingId, bookings.getFirst().getId());
    }

    @Test
    void testGetAllUsersBookingByStatusFuture() {
        User user2 = new User();
        user2.setName("Test User 2");
        user2.setEmail("test2@example.com");
        user2 = userRepository.save(user2);
        Long user2Id = user2.getId();

        BookingInputDto pastBooking = new BookingInputDto();
        pastBooking.setBooker(userId);
        pastBooking.setItemId(itemId);
        pastBooking.setStart(LocalDateTime.now().minusDays(2));
        pastBooking.setEnd(LocalDateTime.now().minusDays(1));
        BookingOutputDto pastBookingDto = bookingService.create(pastBooking, user2Id);
        Long pastBookingId = pastBookingDto.getId();

        BookingInputDto currentBooking = new BookingInputDto();
        currentBooking.setBooker(userId);
        currentBooking.setItemId(itemId);
        currentBooking.setStart(LocalDateTime.now().minusDays(1));
        currentBooking.setEnd(LocalDateTime.now().plusDays(1));
        BookingOutputDto currentBookingDto = bookingService.create(currentBooking, user2Id);
        Long currentBookingId = currentBookingDto.getId();

        BookingInputDto futureBooking = new BookingInputDto();
        futureBooking.setBooker(userId);
        futureBooking.setItemId(itemId);
        futureBooking.setStart(LocalDateTime.now().plusDays(1));
        futureBooking.setEnd(LocalDateTime.now().plusDays(2));
        BookingOutputDto futureBookingDto = bookingService.create(futureBooking, user2Id);
        Long futureBookingId = futureBookingDto.getId();

        List<BookingOutputDto> bookings = bookingService.getAllUsersBookingByStatus(user2Id, StatusForBookingSearch.FUTURE);
        assertFalse(bookings.isEmpty());
        assertEquals(1, bookings.size());
        assertEquals(futureBookingId, bookings.getFirst().getId());
    }

    @Test
    void testGetAllUsersBookingByStatusWaiting() {
        User user2 = new User();
        user2.setName("Test User 2");
        user2.setEmail("test2@example.com");
        user2 = userRepository.save(user2);
        Long user2Id = user2.getId();

        BookingInputDto pastBooking = new BookingInputDto();
        pastBooking.setBooker(userId);
        pastBooking.setItemId(itemId);
        pastBooking.setStart(LocalDateTime.now().minusDays(2));
        pastBooking.setEnd(LocalDateTime.now().minusDays(1));
        BookingOutputDto pastBookingDto = bookingService.create(pastBooking, user2Id);
        Long pastBookingId = pastBookingDto.getId();

        BookingInputDto currentBooking = new BookingInputDto();
        currentBooking.setBooker(userId);
        currentBooking.setItemId(itemId);
        currentBooking.setStart(LocalDateTime.now().minusDays(1));
        currentBooking.setEnd(LocalDateTime.now().plusDays(1));
        BookingOutputDto currentBookingDto = bookingService.create(currentBooking, user2Id);
        Long currentBookingId = currentBookingDto.getId();

        BookingInputDto futureBooking = new BookingInputDto();
        futureBooking.setBooker(userId);
        futureBooking.setItemId(itemId);
        futureBooking.setStart(LocalDateTime.now().plusDays(1));
        futureBooking.setEnd(LocalDateTime.now().plusDays(2));
        BookingOutputDto futureBookingDto = bookingService.create(futureBooking, user2Id);
        Long futureBookingId = futureBookingDto.getId();

        bookingService.updateBookingStatus(pastBookingId, userId, true);
        bookingService.updateBookingStatus(currentBookingId, userId, true);

        List<BookingOutputDto> bookings = bookingService.getAllUsersBookingByStatus(user2Id, StatusForBookingSearch.WAITING);
        assertFalse(bookings.isEmpty());
        assertEquals(1, bookings.size());
        assertEquals(futureBookingId, bookings.getFirst().getId());
    }

    @Test
    void testGetAllUsersBookingByStatusRejected() {
        User user2 = new User();
        user2.setName("Test User 2");
        user2.setEmail("test2@example.com");
        user2 = userRepository.save(user2);
        Long user2Id = user2.getId();

        BookingInputDto pastBooking = new BookingInputDto();
        pastBooking.setBooker(userId);
        pastBooking.setItemId(itemId);
        pastBooking.setStart(LocalDateTime.now().minusDays(2));
        pastBooking.setEnd(LocalDateTime.now().minusDays(1));
        BookingOutputDto pastBookingDto = bookingService.create(pastBooking, user2Id);
        Long pastBookingId = pastBookingDto.getId();

        BookingInputDto currentBooking = new BookingInputDto();
        currentBooking.setBooker(userId);
        currentBooking.setItemId(itemId);
        currentBooking.setStart(LocalDateTime.now().minusDays(1));
        currentBooking.setEnd(LocalDateTime.now().plusDays(1));
        BookingOutputDto currentBookingDto = bookingService.create(currentBooking, user2Id);
        Long currentBookingId = currentBookingDto.getId();

        BookingInputDto futureBooking = new BookingInputDto();
        futureBooking.setBooker(userId);
        futureBooking.setItemId(itemId);
        futureBooking.setStart(LocalDateTime.now().plusDays(1));
        futureBooking.setEnd(LocalDateTime.now().plusDays(2));
        BookingOutputDto futureBookingDto = bookingService.create(futureBooking, user2Id);
        Long futureBookingId = futureBookingDto.getId();

        bookingService.updateBookingStatus(pastBookingId, userId, true);
        bookingService.updateBookingStatus(currentBookingId, userId, false);

        List<BookingOutputDto> bookings = bookingService.getAllUsersBookingByStatus(user2Id, StatusForBookingSearch.REJECTED);
        assertFalse(bookings.isEmpty());
        assertEquals(1, bookings.size());
        assertEquals(currentBookingId, bookings.getFirst().getId());
    }

    @Test
    void testUpdateBookingStatus() {
        BookingOutputDto createdBooking = bookingService.create(bookingInputDto, userId);
        BookingOutputDto updatedBooking = bookingService.updateBookingStatus(createdBooking.getId(), userId, true);
        assertEquals(Status.APPROVED, updatedBooking.getStatus());
    }

    @Test
    void testGetAllBookingForUserItemsByStatus() {
        bookingService.create(bookingInputDto, userId);
        List<BookingOutputDto> bookings = bookingService.getAllBookingForUserItemsByStatus(userId, StatusForBookingSearch.ALL);
        assertFalse(bookings.isEmpty());
        assertEquals(1, bookings.size());
    }

    @Test
    void testGetAllBookingForUserItemsByStatusCurrent() {
        User user2 = new User();
        user2.setName("Test User 2");
        user2.setEmail("test2@example.com");
        user2 = userRepository.save(user2);
        Long user2Id = user2.getId();

        BookingInputDto pastBooking = new BookingInputDto();
        pastBooking.setBooker(userId);
        pastBooking.setItemId(itemId);
        pastBooking.setStart(LocalDateTime.now().minusDays(2));
        pastBooking.setEnd(LocalDateTime.now().minusDays(1));
        BookingOutputDto pastBookingDto = bookingService.create(pastBooking, user2Id);
        Long pastBookingId = pastBookingDto.getId();

        BookingInputDto currentBooking = new BookingInputDto();
        currentBooking.setBooker(userId);
        currentBooking.setItemId(itemId);
        currentBooking.setStart(LocalDateTime.now().minusDays(1));
        currentBooking.setEnd(LocalDateTime.now().plusDays(1));
        BookingOutputDto currentBookingDto = bookingService.create(currentBooking, user2Id);
        Long currentBookingId = currentBookingDto.getId();

        BookingInputDto futureBooking = new BookingInputDto();
        futureBooking.setBooker(userId);
        futureBooking.setItemId(itemId);
        futureBooking.setStart(LocalDateTime.now().plusDays(1));
        futureBooking.setEnd(LocalDateTime.now().plusDays(2));
        BookingOutputDto futureBookingDto = bookingService.create(futureBooking, user2Id);
        Long futureBookingId = futureBookingDto.getId();

        List<BookingOutputDto> bookings = bookingService.getAllBookingForUserItemsByStatus(userId, StatusForBookingSearch.CURRENT);
        assertFalse(bookings.isEmpty());
        assertEquals(1, bookings.size());
        assertEquals(currentBookingId, bookings.getFirst().getId());
    }

    @Test
    void testGetAllBookingForUserItemsByStatusPast() {
        User user2 = new User();
        user2.setName("Test User 2");
        user2.setEmail("test2@example.com");
        user2 = userRepository.save(user2);
        Long user2Id = user2.getId();

        BookingInputDto pastBooking = new BookingInputDto();
        pastBooking.setBooker(userId);
        pastBooking.setItemId(itemId);
        pastBooking.setStart(LocalDateTime.now().minusDays(2));
        pastBooking.setEnd(LocalDateTime.now().minusDays(1));
        BookingOutputDto pastBookingDto = bookingService.create(pastBooking, user2Id);
        Long pastBookingId = pastBookingDto.getId();

        BookingInputDto currentBooking = new BookingInputDto();
        currentBooking.setBooker(userId);
        currentBooking.setItemId(itemId);
        currentBooking.setStart(LocalDateTime.now().minusDays(1));
        currentBooking.setEnd(LocalDateTime.now().plusDays(1));
        BookingOutputDto currentBookingDto = bookingService.create(currentBooking, user2Id);
        Long currentBookingId = currentBookingDto.getId();

        BookingInputDto futureBooking = new BookingInputDto();
        futureBooking.setBooker(userId);
        futureBooking.setItemId(itemId);
        futureBooking.setStart(LocalDateTime.now().plusDays(1));
        futureBooking.setEnd(LocalDateTime.now().plusDays(2));
        BookingOutputDto futureBookingDto = bookingService.create(futureBooking, user2Id);
        Long futureBookingId = futureBookingDto.getId();

        List<BookingOutputDto> bookings = bookingService.getAllBookingForUserItemsByStatus(userId, StatusForBookingSearch.PAST);
        assertFalse(bookings.isEmpty());
        assertEquals(1, bookings.size());
        assertEquals(pastBookingId, bookings.getFirst().getId());
    }

    @Test
    void testGetAllBookingForUserItemsByStatusFuture() {
        User user2 = new User();
        user2.setName("Test User 2");
        user2.setEmail("test2@example.com");
        user2 = userRepository.save(user2);
        Long user2Id = user2.getId();

        BookingInputDto pastBooking = new BookingInputDto();
        pastBooking.setBooker(userId);
        pastBooking.setItemId(itemId);
        pastBooking.setStart(LocalDateTime.now().minusDays(2));
        pastBooking.setEnd(LocalDateTime.now().minusDays(1));
        BookingOutputDto pastBookingDto = bookingService.create(pastBooking, user2Id);
        Long pastBookingId = pastBookingDto.getId();

        BookingInputDto currentBooking = new BookingInputDto();
        currentBooking.setBooker(userId);
        currentBooking.setItemId(itemId);
        currentBooking.setStart(LocalDateTime.now().minusDays(1));
        currentBooking.setEnd(LocalDateTime.now().plusDays(1));
        BookingOutputDto currentBookingDto = bookingService.create(currentBooking, user2Id);
        Long currentBookingId = currentBookingDto.getId();

        BookingInputDto futureBooking = new BookingInputDto();
        futureBooking.setBooker(userId);
        futureBooking.setItemId(itemId);
        futureBooking.setStart(LocalDateTime.now().plusDays(1));
        futureBooking.setEnd(LocalDateTime.now().plusDays(2));
        BookingOutputDto futureBookingDto = bookingService.create(futureBooking, user2Id);
        Long futureBookingId = futureBookingDto.getId();

        List<BookingOutputDto> bookings = bookingService.getAllBookingForUserItemsByStatus(userId, StatusForBookingSearch.FUTURE);
        assertFalse(bookings.isEmpty());
        assertEquals(1, bookings.size());
        assertEquals(futureBookingId, bookings.getFirst().getId());
    }

    @Test
    void testGetAllBookingForUserItemsByStatusWaiting() {
        User user2 = new User();
        user2.setName("Test User 2");
        user2.setEmail("test2@example.com");
        user2 = userRepository.save(user2);
        Long user2Id = user2.getId();

        BookingInputDto pastBooking = new BookingInputDto();
        pastBooking.setBooker(userId);
        pastBooking.setItemId(itemId);
        pastBooking.setStart(LocalDateTime.now().minusDays(2));
        pastBooking.setEnd(LocalDateTime.now().minusDays(1));
        BookingOutputDto pastBookingDto = bookingService.create(pastBooking, user2Id);
        Long pastBookingId = pastBookingDto.getId();

        BookingInputDto currentBooking = new BookingInputDto();
        currentBooking.setBooker(userId);
        currentBooking.setItemId(itemId);
        currentBooking.setStart(LocalDateTime.now().minusDays(1));
        currentBooking.setEnd(LocalDateTime.now().plusDays(1));
        BookingOutputDto currentBookingDto = bookingService.create(currentBooking, user2Id);
        Long currentBookingId = currentBookingDto.getId();

        BookingInputDto futureBooking = new BookingInputDto();
        futureBooking.setBooker(userId);
        futureBooking.setItemId(itemId);
        futureBooking.setStart(LocalDateTime.now().plusDays(1));
        futureBooking.setEnd(LocalDateTime.now().plusDays(2));
        BookingOutputDto futureBookingDto = bookingService.create(futureBooking, user2Id);
        Long futureBookingId = futureBookingDto.getId();

        bookingService.updateBookingStatus(pastBookingId, userId, true);
        bookingService.updateBookingStatus(currentBookingId, userId, true);

        List<BookingOutputDto> bookings = bookingService.getAllBookingForUserItemsByStatus(userId, StatusForBookingSearch.WAITING);
        assertFalse(bookings.isEmpty());
        assertEquals(1, bookings.size());
        assertEquals(futureBookingId, bookings.getFirst().getId());
    }

    @Test
    void testGetAllBookingForUserItemsByStatusRejected() {
        User user2 = new User();
        user2.setName("Test User 2");
        user2.setEmail("test2@example.com");
        user2 = userRepository.save(user2);
        Long user2Id = user2.getId();

        BookingInputDto pastBooking = new BookingInputDto();
        pastBooking.setBooker(userId);
        pastBooking.setItemId(itemId);
        pastBooking.setStart(LocalDateTime.now().minusDays(2));
        pastBooking.setEnd(LocalDateTime.now().minusDays(1));
        BookingOutputDto pastBookingDto = bookingService.create(pastBooking, user2Id);
        Long pastBookingId = pastBookingDto.getId();

        BookingInputDto currentBooking = new BookingInputDto();
        currentBooking.setBooker(userId);
        currentBooking.setItemId(itemId);
        currentBooking.setStart(LocalDateTime.now().minusDays(1));
        currentBooking.setEnd(LocalDateTime.now().plusDays(1));
        BookingOutputDto currentBookingDto = bookingService.create(currentBooking, user2Id);
        Long currentBookingId = currentBookingDto.getId();

        BookingInputDto futureBooking = new BookingInputDto();
        futureBooking.setBooker(userId);
        futureBooking.setItemId(itemId);
        futureBooking.setStart(LocalDateTime.now().plusDays(1));
        futureBooking.setEnd(LocalDateTime.now().plusDays(2));
        BookingOutputDto futureBookingDto = bookingService.create(futureBooking, user2Id);
        Long futureBookingId = futureBookingDto.getId();

        bookingService.updateBookingStatus(pastBookingId, userId, true);
        bookingService.updateBookingStatus(currentBookingId, userId, false);

        List<BookingOutputDto> bookings = bookingService.getAllBookingForUserItemsByStatus(userId, StatusForBookingSearch.REJECTED);
        assertFalse(bookings.isEmpty());
        assertEquals(1, bookings.size());
        assertEquals(currentBookingId, bookings.getFirst().getId());
    }

    @Test
    void testGetByIdWithInvalidUserId() {
        BookingOutputDto createdBooking = bookingService.create(bookingInputDto, userId);
        assertThrows(NotFoundException.class, () -> bookingService.getById(createdBooking.getId(), 999L));
    }

    @Test
    void testCreateBookingWithUnavailableItem() {
        item.setAvailable(false);
        itemRepository.save(item);
        assertThrows(ConflictException.class, () -> bookingService.create(bookingInputDto, userId));
    }

    @Test
    void testCreateBookingWithNonExistentUser() {
        assertThrows(NotFoundException.class, () -> bookingService.create(bookingInputDto, 999L));
    }

    @Test
    void testCreateBookingWithNonExistentItem() {
        bookingInputDto.setItemId(999L);
        assertThrows(NotFoundException.class, () -> bookingService.create(bookingInputDto, userId));
    }

}
