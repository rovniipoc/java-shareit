package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.validation.CreateGroup;

@Data
public class ItemInputDto {

    @NotBlank(groups = CreateGroup.class)
    private String name;

    @NotBlank(groups = CreateGroup.class)
    private String description;

    @NotNull(groups = CreateGroup.class)
    private Boolean available;

    private Long owner;

    private ItemRequest request;
}
