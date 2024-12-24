package ru.practicum.shareit.booking;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.practicum.shareit.booking.dto.BookingInputDto;
import ru.practicum.shareit.validation.CreateGroup;


@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {
    private final BookingClient bookingClient;
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader(USER_ID_HEADER) Long userId,
                                   @Validated(CreateGroup.class) @RequestBody BookingInputDto bookingInputDto) {
        log.info("Поступил запрос Post /bookings от пользователя с id = {} на добавление Booking с телом {}", userId, bookingInputDto);
        ResponseEntity<Object> response = bookingClient.create(bookingInputDto, userId);
        log.info("Сформирован ответ Post /bookings с телом: {}", response);
        return response;
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> updateBookingStatus(@RequestHeader(USER_ID_HEADER) Long userId,
                                                @PathVariable Long bookingId,
                                                @RequestParam Boolean approved) {
        log.info("Поступил запрос Patch /bookings/{} от пользователя с id = {} на изменение статуса Booking с id = {} на {}", bookingId, userId, bookingId, approved);
        ResponseEntity<Object> response = bookingClient.updateBookingStatus(bookingId, userId, approved);
        log.info("Сформирован ответ Patch /bookings/{} с телом: {}", bookingId, response);
        return response;
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBookingById(@RequestHeader(USER_ID_HEADER) Long userId,
                                           @PathVariable Long bookingId) {
        log.info("Поступил запрос Get /bookings/{} от пользователя с id = {} на получение Booking с id = {}", bookingId, userId, bookingId);
        ResponseEntity<Object> response = bookingClient.getById(bookingId, userId);
        log.info("Сформирован ответ Get /bookings/{} с телом: {}", bookingId, response);
        return response;
    }

    // получение списка всех бронирований текущего пользователя
    @GetMapping
    public ResponseEntity<Object> getAllUsersBookingByStatus(@RequestHeader(USER_ID_HEADER) Long userId,
                                                             @RequestParam(defaultValue = "ALL") StatusForBookingSearch state) {
        log.info("Поступил запрос Get /bookings от пользователя с id = {} на получение List<Booking> с параметром = {}", userId, state);
        ResponseEntity<Object> response = bookingClient.getAllUsersBookingByStatus(userId, state);
        log.info("Сформирован ответ Get /bookings с телом: {}", response);
        return response;
    }

    // получение списка бронирований для всех вещей текущего пользователя
    @GetMapping("/owner")
    public ResponseEntity<Object> getAllBookingForUserItemsByStatus(@RequestHeader(USER_ID_HEADER) Long userId,
                                                                    @RequestParam(defaultValue = "ALL") StatusForBookingSearch state) {
        log.info("Поступил запрос Get /bookings/owner от пользователя с id = {} на получение List<Booking> с параметром = {}", userId, state);
        ResponseEntity<Object> response = bookingClient.getAllBookingForUserItemsByStatus(userId, state);
        log.info("Сформирован ответ Get /bookings/owner с телом: {}", response);
        return response;
    }
}