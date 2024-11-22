package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.validation.CreateItemGroup;

@Data
public class ItemDto {
    @Positive
    private Long id;
    @NotBlank(groups = CreateItemGroup.class)
    private String name;
    @NotBlank(groups = CreateItemGroup.class)
    private String description;
    @NotNull(groups = CreateItemGroup.class)
    private Boolean available;
    private Long owner;
    private ItemRequest request;
}
