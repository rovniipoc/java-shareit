package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.request.dto.ItemRequestInputDto;
import ru.practicum.shareit.request.dto.ItemRequestOutputDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@Transactional
public class ItemRequestServiceTests {

    @Autowired
    private ItemRequestService itemRequestService;

    @Autowired
    private ItemRequestRepository itemRequestRepository;

    @Autowired
    private UserRepository userRepository;

    private ItemRequestInputDto itemRequestInputDto;
    private ItemRequestOutputDto itemRequestOutputDto;
    private Long userId;
    private Long requestId;

    @BeforeEach
    void setUp() {
        User user = new User();
        user.setName("Test User");
        user.setEmail("test@example.com");
        user = userRepository.save(user);
        userId = user.getId();

        itemRequestInputDto = new ItemRequestInputDto();
        itemRequestInputDto.setDescription("Test Request");

        itemRequestOutputDto = itemRequestService.create(itemRequestInputDto, userId);
        requestId = itemRequestOutputDto.getId();
    }

    @Test
    void testCreateItemRequest() {
        ItemRequestInputDto newItemRequestInputDto = new ItemRequestInputDto();
        newItemRequestInputDto.setDescription("New Test Request");

        ItemRequestOutputDto newItemRequestOutputDto = itemRequestService.create(newItemRequestInputDto, userId);
        assertNotNull(newItemRequestOutputDto);
        assertEquals(newItemRequestInputDto.getDescription(), newItemRequestOutputDto.getDescription());
    }

    @Test
    void testGetAll() {
        List<ItemRequestOutputDto> requests = itemRequestService.getAll();
        assertFalse(requests.isEmpty());
        assertEquals(1, requests.size());
        assertEquals(itemRequestOutputDto.getId(), requests.get(0).getId());
    }

    @Test
    void testGetAllDetailedByUser() {
        List<ItemRequestOutputDto> requests = itemRequestService.getAllDetailedByUser(userId);
        assertFalse(requests.isEmpty());
        assertEquals(1, requests.size());
        assertEquals(itemRequestOutputDto.getId(), requests.get(0).getId());
    }

    @Test
    void testGetOneDetailedById() {
        ItemRequestOutputDto request = itemRequestService.getOneDetailedById(requestId);
        assertNotNull(request);
        assertEquals(itemRequestOutputDto.getId(), request.getId());
        assertEquals(itemRequestOutputDto.getDescription(), request.getDescription());
    }

    @Test
    void testGetOneDetailedByIdNotFound() {
        assertThrows(NotFoundException.class, () -> itemRequestService.getOneDetailedById(999L));
    }
}
