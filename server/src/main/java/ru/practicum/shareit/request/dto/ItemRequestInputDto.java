package ru.practicum.shareit.request.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import ru.practicum.shareit.validation.CreateGroup;
import ru.practicum.shareit.validation.UpdateGroup;

@Data
public class ItemRequestInputDto {
    @NotBlank(groups = {CreateGroup.class, UpdateGroup.class})
    private String description;
}
