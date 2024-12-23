package ru.practicum.shareit.item.dto;

import lombok.Data;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

@Data
public class CommentInputDto {

    private String text;
    private Item item;
    private User author;
    private LocalDateTime created;
}
