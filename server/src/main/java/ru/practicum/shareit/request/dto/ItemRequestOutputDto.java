package ru.practicum.shareit.request.dto;

import lombok.Data;
import ru.practicum.shareit.item.dto.ItemOutputDto;
import ru.practicum.shareit.user.dto.UserOutputDto;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ItemRequestOutputDto {
    private Long id;
    private String description;
    private UserOutputDto requestor;
    private LocalDateTime created;
    private List<ItemOutputDto> items;
}
