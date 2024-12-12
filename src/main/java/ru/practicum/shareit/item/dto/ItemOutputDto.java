package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.validation.CreateGroup;

import java.util.List;

@Data
public class ItemOutputDto {
    @Positive
    private Long id;
    @NotBlank(groups = CreateGroup.class)
    private String name;
    @NotBlank(groups = CreateGroup.class)
    private String description;
    @NotNull(groups = CreateGroup.class)
    private Boolean available;
    private Long owner;
    private ItemRequest request;
    private Booking lastBooking;
    private Booking nextBooking;
    private List<Comment> comments;
}
