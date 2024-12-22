package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingInputDto;
import ru.practicum.shareit.booking.dto.BookingOutputDto;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingService bookingService;
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    @PostMapping
    public BookingOutputDto create(@RequestHeader(USER_ID_HEADER) Long userId,
                                   @RequestBody BookingInputDto bookingInputDto) {
        log.info("Поступил запрос Post /bookings от пользователя с id = {} на добавление Booking с телом {}", userId, bookingInputDto);
        BookingOutputDto newBooking = bookingService.create(bookingInputDto, userId);
        log.info("Сформирован ответ Post /bookings с телом: {}", newBooking);
        return newBooking;
    }

    @PatchMapping("/{bookingId}")
    public BookingOutputDto updateBookingStatus(@RequestHeader(USER_ID_HEADER) Long userId,
                                                @PathVariable Long bookingId,
                                                @RequestParam Boolean approved) {
        log.info("Поступил запрос Patch /bookings/{} от пользователя с id = {} на изменение статуса Booking с id = {} на {}", bookingId, userId, bookingId, approved);
        BookingOutputDto booking = bookingService.updateBookingStatus(bookingId, userId, approved);
        log.info("Сформирован ответ Patch /bookings/{} с телом: {}", bookingId, booking);
        return booking;
    }

    @GetMapping("/{bookingId}")
    public BookingOutputDto getBookingById(@RequestHeader(USER_ID_HEADER) Long userId,
                                           @PathVariable Long bookingId) {
        log.info("Поступил запрос Get /bookings/{} от пользователя с id = {} на получение Booking с id = {}", bookingId, userId, bookingId);
        BookingOutputDto booking = bookingService.getById(bookingId, userId);
        log.info("Сформирован ответ Get /bookings/{} с телом: {}", bookingId, booking);
        return booking;
    }

    // получение списка всех бронирований текущего пользователя
    @GetMapping
    public List<BookingOutputDto> getAllUsersBookingByStatus(@RequestHeader(USER_ID_HEADER) Long userId,
                                                             @RequestParam(defaultValue = "ALL") StatusForBookingSearch state) {
        log.info("Поступил запрос Get /bookings от пользователя с id = {} на получение List<Booking> с параметром = {}", userId, state);
        List<BookingOutputDto> bookings = bookingService.getAllUsersBookingByStatus(userId, state);
        log.info("Сформирован ответ Get /bookings с телом: {}", bookings);
        return bookings;
    }

    // получение списка бронирований для всех вещей текущего пользователя
    @GetMapping("/owner")
    public List<BookingOutputDto> getAllBookingForUserItemsByStatus(@RequestHeader(USER_ID_HEADER) Long userId,
                                                                    @RequestParam(defaultValue = "ALL") StatusForBookingSearch state) {
        log.info("Поступил запрос Get /bookings/owner от пользователя с id = {} на получение List<Booking> с параметром = {}", userId, state);
        List<BookingOutputDto> bookings = bookingService.getAllBookingForUserItemsByStatus(userId, state);
        log.info("Сформирован ответ Get /bookings/owner с телом: {}", bookings);
        return bookings;
    }
}
