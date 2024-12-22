package ru.practicum.shareit.booking.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.validation.CreateGroup;

import java.time.LocalDateTime;

@Data
public class BookingInputDto {

    @NotNull(groups = CreateGroup.class)
    @Future(groups = CreateGroup.class)
    private LocalDateTime start;

    @NotNull(groups = CreateGroup.class)
    @Future(groups = CreateGroup.class)
    private LocalDateTime end;

    @Positive(groups = CreateGroup.class)
    private Long itemId;

    private Long booker;

    private Status status;

    @AssertTrue(groups = CreateGroup.class, message = "Время начала бронирования должно предшествовать времени его завершения")
    public boolean isStartBeforeEnd() {
        if (start == null || end == null) {
            return false;
        }
        return start.isBefore(end);
    }

}
