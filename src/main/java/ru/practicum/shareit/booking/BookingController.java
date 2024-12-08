package ru.practicum.shareit.booking;

import jakarta.websocket.server.PathParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.validation.CreateGroup;

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
        return bookingService.updateBookingStatus(bookingId, userId, approved);
    }

}
