package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingInputDto;
import ru.practicum.shareit.booking.dto.BookingOutputDto;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.ItemOutputDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.dto.UserOutputDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
public class BookingControllerTests {

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    private BookingService bookingService;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private ItemRepository itemRepository;

    @Autowired
    private MockMvc mvc;

    private Booking booking;
    private BookingInputDto bookingInputDto;
    private BookingOutputDto bookingOutputDto;
    private Item item;
    private ItemOutputDto itemOutputDto;
    private User user;
    private UserOutputDto userOutputDto;
    private List<BookingOutputDto> bookingOutputDtoList;
    private List<ItemOutputDto> itemOutputDtoList;
    private List<UserOutputDto> userOutputDtoList;

    private static final Long ENTITIES_COUNT = 10L;
    private static final Random RANDOM = new Random();

    @BeforeEach
    void setUp() {

        itemOutputDto = ItemOutputDto.builder()
                .id(1L)
                .name(generateRandomString(5))
                .description(generateRandomString(5))
                .available(true)
                .owner(1L)
                .request(null)
                .lastBooking(null)
                .nextBooking(null)
                .comments(null)
                .build();

        Item item = new Item();
        item.setId(itemOutputDto.getId());
        item.setName(itemOutputDto.getName());
        item.setDescription(itemOutputDto.getDescription());
        item.setAvailable(itemOutputDto.getAvailable());
        item.setOwner(itemOutputDto.getOwner());
        item.setRequest(null);

        itemOutputDtoList = new ArrayList<>();
        for (long i = 1; i <= ENTITIES_COUNT; i++) {
            itemOutputDtoList.add(ItemOutputDto.builder()
                    .id(i)
                    .name(generateRandomString(5))
                    .description(generateRandomString(5))
                    .available(true)
                    .owner(i)
                    .lastBooking(null)
                    .nextBooking(null)
                    .comments(null)
                    .build());
        }

        userOutputDto = UserOutputDto.builder()
                .id(1L)
                .name(generateRandomString(5))
                .email(generateRandomEmail(5))
                .build();

        User user = new User();
        user.setId(userOutputDto.getId());
        user.setName(userOutputDto.getName());
        user.setEmail(userOutputDto.getEmail());

        userOutputDtoList = new ArrayList<>();
        for (long i = 1; i <= ENTITIES_COUNT; i++) {
            userOutputDtoList.add(UserOutputDto.builder()
                    .id(i)
                    .name(generateRandomString(5))
                    .email(generateRandomEmail(5))
                    .build());
        }

        bookingOutputDto = BookingOutputDto.builder()
                .id(1L)
                .start(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS).plusMinutes(1))
                .end(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS).plusDays(1))
                .item(itemOutputDto)
                .booker(userOutputDto)
                .build();

        bookingInputDto = new BookingInputDto();
        bookingInputDto.setStart(bookingOutputDto.getStart());
        bookingInputDto.setEnd(bookingOutputDto.getEnd());
        bookingInputDto.setItemId(bookingOutputDto.getItem().getId());
        bookingInputDto.setBooker(bookingOutputDto.getBooker().getId());
        bookingInputDto.setStatus(bookingOutputDto.getStatus());

        bookingOutputDtoList = new ArrayList<>();
        for (long i = 1; i <= ENTITIES_COUNT; i++) {
            bookingOutputDtoList.add(BookingOutputDto.builder()
                    .id(i)
                    .start(LocalDateTime.now().plusMinutes(1))
                    .end(LocalDateTime.now().plusDays(1))
                    .item(itemOutputDto)
                    .booker(userOutputDto)
                    .status(Status.WAITING)
                    .build());
        }
    }

    @Test
    void createBookingTest() throws Exception {
        when(bookingService.create(any(), anyLong()))
                .thenReturn(bookingOutputDto);
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(item));

        mvc.perform(post("/bookings")
                        .content(objectMapper.writeValueAsString(bookingInputDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", bookingOutputDto.getBooker().getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingOutputDto.getId()), Long.class))
                .andExpect(jsonPath("$.start", is(bookingOutputDto.getStart().truncatedTo(ChronoUnit.SECONDS).toString())))
                .andExpect(jsonPath("$.end", is(bookingOutputDto.getEnd().truncatedTo(ChronoUnit.SECONDS).toString())))
                .andExpect(jsonPath("$.item.id", is(bookingOutputDto.getItem().getId()), Long.class))
                .andExpect(jsonPath("$.item.name", is(bookingOutputDto.getItem().getName())))
                .andExpect(jsonPath("$.booker.id", is(bookingOutputDto.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$.booker.name", is(bookingOutputDto.getBooker().getName())))
                .andExpect(jsonPath("$.status", is(bookingOutputDto.getStatus())));

    }

    private String generateRandomString(int targetStringLength) {
        int leftLimit = 97; // letter 'a'
        int rightLimit = 122; // letter 'z'
        return RANDOM.ints(leftLimit, rightLimit + 1)
                .limit(targetStringLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }

    private String generateRandomEmail(int targetStringLength) {
        return generateRandomString(targetStringLength) + "@example.com";
    }

    private LocalDateTime generateRandomDate() {
        long minDay = LocalDateTime.of(2000, 1, 1, 0, 0).toEpochSecond(ZoneOffset.UTC);
        long maxDay = LocalDateTime.of(2023, 12, 31, 23, 59).toEpochSecond(ZoneOffset.UTC);
        long randomDay = minDay + RANDOM.nextLong() % (maxDay - minDay);
        return LocalDateTime.ofEpochSecond(randomDay, 0, ZoneOffset.UTC);
    }
}
