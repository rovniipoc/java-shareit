package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.CommentInputDto;
import ru.practicum.shareit.item.dto.CommentOutputDto;
import ru.practicum.shareit.item.dto.ItemInputDto;
import ru.practicum.shareit.item.dto.ItemOutputDto;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@Transactional
public class ItemServiceTests {

    @Autowired
    private ItemService itemService;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRequestRepository itemRequestRepository;

    @Autowired
    private BookingRepository bookingRepository;

    private ItemInputDto itemInputDto;
    private ItemOutputDto itemOutputDto;
    private CommentInputDto commentInputDto;
    private CommentOutputDto commentOutputDto;
    private Long userId;
    private Long itemId;
    private Long itemRequestId;
    private Long bookingId;

    @BeforeEach
    void setUp() {
        User user = new User();
        user.setName("Test User");
        user.setEmail("test@example.com");
        user = userRepository.save(user);
        userId = user.getId();

        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setDescription("Test Request");
        itemRequest.setRequestor(user);
        itemRequest = itemRequestRepository.save(itemRequest);
        itemRequestId = itemRequest.getId();

        itemInputDto = new ItemInputDto();
        itemInputDto.setName("Test Item");
        itemInputDto.setDescription("Test Description");
        itemInputDto.setAvailable(true);
        itemInputDto.setRequestId(itemRequestId);

        itemOutputDto = itemService.create(userId, itemInputDto);
        itemId = itemOutputDto.getId();

        commentInputDto = new CommentInputDto();
        commentInputDto.setText("Test Comment");

        // Создание завершенной аренды
        Booking booking = new Booking();
        booking.setItem(itemRepository.findById(itemId).orElseThrow());
        booking.setBooker(user);
        booking.setStart(LocalDateTime.now().minusDays(2));
        booking.setEnd(LocalDateTime.now().minusDays(1));
        booking.setStatus(Status.APPROVED);
        booking = bookingRepository.save(booking);
        bookingId = booking.getId();
    }

    @Test
    void testGetAllByUserId() {
        List<ItemOutputDto> items = itemService.getAllByUserId(userId);
        assertFalse(items.isEmpty());
        assertEquals(1, items.size());
        assertEquals(itemOutputDto.getId(), items.get(0).getId());
    }

    @Test
    void testGetById() {
        ItemOutputDto item = itemService.getById(itemId);
        assertNotNull(item);
        assertEquals(itemOutputDto.getId(), item.getId());
        assertEquals(itemOutputDto.getName(), item.getName());
        assertEquals(itemOutputDto.getDescription(), item.getDescription());
        assertEquals(itemOutputDto.getAvailable(), item.getAvailable());
    }

    @Test
    void testFindByText() {
        List<ItemOutputDto> items = itemService.findByText("Test");
        assertFalse(items.isEmpty());
        assertEquals(1, items.size());
        assertEquals(itemOutputDto.getId(), items.get(0).getId());
    }

    @Test
    void testCreateItem() {
        ItemInputDto newItemInputDto = new ItemInputDto();
        newItemInputDto.setName("New Test Item");
        newItemInputDto.setDescription("New Test Description");
        newItemInputDto.setAvailable(true);

        ItemOutputDto newItemOutputDto = itemService.create(userId, newItemInputDto);
        assertNotNull(newItemOutputDto);
        assertEquals(newItemInputDto.getName(), newItemOutputDto.getName());
        assertEquals(newItemInputDto.getDescription(), newItemOutputDto.getDescription());
        assertEquals(newItemInputDto.getAvailable(), newItemOutputDto.getAvailable());
    }

    @Test
    void testUpdateItem() {
        ItemInputDto updatedItemInputDto = new ItemInputDto();
        updatedItemInputDto.setName("Updated Test Item");
        updatedItemInputDto.setDescription("Updated Test Description");
        updatedItemInputDto.setAvailable(false);

        ItemOutputDto updatedItemOutputDto = itemService.update(userId, itemId, updatedItemInputDto);
        assertNotNull(updatedItemOutputDto);
        assertEquals(updatedItemInputDto.getName(), updatedItemOutputDto.getName());
        assertEquals(updatedItemInputDto.getDescription(), updatedItemOutputDto.getDescription());
        assertEquals(updatedItemInputDto.getAvailable(), updatedItemOutputDto.getAvailable());
    }

    @Test
    void testDeleteItem() {
        itemService.deleteById(userId, itemId);
        assertThrows(NotFoundException.class, () -> itemService.getById(itemId));
    }

    @Test
    void testCreateComment() {
        CommentOutputDto comment = itemService.createComment(userId, itemId, commentInputDto);
        assertNotNull(comment);
        assertEquals(commentInputDto.getText(), comment.getText());
    }

    @Test
    void testCreateCommentWithNonExistentUser() {
        assertThrows(NotFoundException.class, () -> itemService.createComment(999L, itemId, commentInputDto));
    }

    @Test
    void testCreateCommentWithNonExistentItem() {
        assertThrows(NotFoundException.class, () -> itemService.createComment(userId, 999L, commentInputDto));
    }

    @Test
    void testCreateCommentWithNonBookedItem() {
        // Удаление завершенной аренды для проверки исключения
        bookingRepository.deleteById(bookingId);
        assertThrows(ConflictException.class, () -> itemService.createComment(userId, itemId, commentInputDto));
    }

    @Test
    void testUpdateItemWithNonExistentUser() {
        assertThrows(NotFoundException.class, () -> itemService.update(999L, itemId, itemInputDto));
    }

    @Test
    void testUpdateItemWithNonExistentItem() {
        assertThrows(NotFoundException.class, () -> itemService.update(userId, 999L, itemInputDto));
    }

    @Test
    void testUpdateItemWithNonOwnerUser() {
        User newUser = new User();
        newUser.setName("New User");
        newUser.setEmail("newuser@example.com");
        newUser = userRepository.save(newUser);
        Long newUserId = newUser.getId();

        assertThrows(ValidationException.class, () -> itemService.update(newUserId, itemId, itemInputDto));
    }
}