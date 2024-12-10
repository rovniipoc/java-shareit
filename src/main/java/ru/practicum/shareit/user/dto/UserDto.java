package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import ru.practicum.shareit.validation.CreateGroup;
import ru.practicum.shareit.validation.UpdateGroup;

@Data
public class UserDto {
    @Positive
    private Long id;
    @NotBlank(groups = CreateGroup.class)
    private String name;
    @Email(groups = {CreateGroup.class, UpdateGroup.class})
    @NotBlank(groups = CreateGroup.class)
    private String email;
}
