package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.validation.CreateGroup;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
@Validated
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public Booking create(@RequestHeader("X-Sharer-User-Id") Long userId,
                          @Validated(CreateGroup.class) @RequestBody BookingDto bookingDto) {
        log.info("Поступил запрос Post /bookings от пользователя с id = {} на добавление Booking с телом {}", userId, bookingDto);
        Booking newBooking = bookingService.create(bookingDto, userId);
        log.info("Сформирован ответ Post /bookings с телом: {}", newBooking);
        return newBooking;
    }

    @PatchMapping("/{bookingId}")
    public Booking updateBookingStatus(@RequestHeader("X-Sharer-User-Id") Long userId,
                                       @PathVariable Long bookingId,
                                       @RequestParam Boolean approved) {
        log.info("Поступил запрос Patch /bookings/{} от пользователя с id = {} на изменение статуса Booking с id = {} на {}", bookingId, userId, bookingId, approved);
        Booking booking = bookingService.updateBookingStatus(bookingId, userId, approved);
        log.info("Сформирован ответ Patch /bookings/{} с телом: {}", bookingId, booking);
        return booking;
    }

    @GetMapping("/{bookingId}")
    public Booking getBookingById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                  @PathVariable Long bookingId) {
        log.info("Поступил запрос Get /bookings/{} от пользователя с id = {} на получение Booking с id = {}", bookingId, userId, bookingId);
        Booking booking = bookingService.getById(bookingId, userId);
        log.info("Сформирован ответ Get /bookings/{} с телом: {}", bookingId, booking);
        return booking;
    }

    // получение списка всех бронирований текущего пользователя
    @GetMapping
    public List<Booking> getAllUsersBookingByStatus(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                    @RequestParam(defaultValue = "ALL") StatusForBookingSearch state) {
        log.info("Поступил запрос Get /bookings от пользователя с id = {} на получение List<Booking> с параметром = {}", userId, state);
        List<Booking> bookings = bookingService.getAllUsersBookingByStatus(userId, state);
        log.info("Сформирован ответ Get /bookings с телом: {}", bookings);
        return bookings;
    }

    // получение списка бронирований для всех вещей текущего пользователя
    @GetMapping("/owner")
    public List<Booking> getAllBookingForUserItemsByStatus(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                           @RequestParam(defaultValue = "ALL") StatusForBookingSearch state) {
        log.info("Поступил запрос Get /bookings/owner от пользователя с id = {} на получение List<Booking> с параметром = {}", userId, state);
        List<Booking> bookings = bookingService.getAllBookingForUserItemsByStatus(userId, state);
        log.info("Сформирован ответ Get /bookings/owner с телом: {}", bookings);
        return bookings;
    }
}
