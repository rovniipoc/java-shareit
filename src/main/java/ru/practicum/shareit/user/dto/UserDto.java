package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import ru.practicum.shareit.validation.CreateUserGroup;
import ru.practicum.shareit.validation.UpdateUserGroup;

@Data
public class UserDto {
    @Positive
    private Long id;
    @NotBlank(groups = CreateUserGroup.class)
    private String name;
    @Email(groups = {CreateUserGroup.class, UpdateUserGroup.class})
    @NotBlank(groups = CreateUserGroup.class)
    private String email;
}
