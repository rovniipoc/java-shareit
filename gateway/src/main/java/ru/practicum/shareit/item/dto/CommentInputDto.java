package ru.practicum.shareit.item.dto;

import lombok.Data;
import ru.practicum.shareit.user.dto.UserInputDto;

import java.time.LocalDateTime;

@Data
public class CommentInputDto {

    private String text;
    private ItemInputDto item;
    private UserInputDto author;
    private LocalDateTime created;
}
