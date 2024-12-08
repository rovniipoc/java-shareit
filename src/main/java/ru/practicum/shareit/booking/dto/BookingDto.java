package ru.practicum.shareit.booking.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

@Data
public class BookingDto {

    private Long id;

    @NotNull
    private LocalDateTime start = LocalDateTime.now();

    @NotNull
    private LocalDateTime end;

    @Positive
    private Long itemId;

    @Positive
    private Long booker;

    private Status status;
}
