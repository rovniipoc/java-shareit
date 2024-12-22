package ru.practicum.shareit.item.dto;

import lombok.Data;
import ru.practicum.shareit.booking.dto.BookingOutputDto;
import ru.practicum.shareit.request.dto.ItemRequestOutputDto;

import java.util.List;

@Data
public class ItemOutputDto {

    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private Long owner;
    private ItemRequestOutputDto request;
    private BookingOutputDto lastBooking;
    private BookingOutputDto nextBooking;
    private List<CommentOutputDto> comments;
}
