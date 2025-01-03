package ru.practicum.shareit.booking.dto;

import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserMapper;

import java.util.ArrayList;
import java.util.List;

public class BookingMapper {
    public static BookingOutputDto toBookingOutputDto(Booking booking) {
        BookingOutputDto bookingOutputDto = BookingOutputDto.builder().build();
        bookingOutputDto.setId(booking.getId());
        bookingOutputDto.setStart(booking.getStart());
        bookingOutputDto.setEnd(booking.getEnd());
        bookingOutputDto.setItem(ItemMapper.toItemOutputDto(booking.getItem()));
        bookingOutputDto.setBooker(UserMapper.toUserOutputDto(booking.getBooker()));
        bookingOutputDto.setStatus(booking.getStatus());

        return bookingOutputDto;
    }

    public static List<BookingOutputDto> toBookingOutputDto(Iterable<Booking> bookings) {
        List<BookingOutputDto> result = new ArrayList<>();

        for (Booking booking : bookings) {
            result.add(toBookingOutputDto(booking));
        }

        return result;
    }

    public static Booking toBooking(BookingInputDto bookingInputDto, User user, Item item) {
        Booking booking = new Booking();
        booking.setStart(bookingInputDto.getStart());
        booking.setEnd(bookingInputDto.getEnd());
        booking.setItem(item);
        booking.setBooker(user);

        return booking;
    }
}
