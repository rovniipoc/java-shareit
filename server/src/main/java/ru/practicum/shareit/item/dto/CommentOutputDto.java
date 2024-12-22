package ru.practicum.shareit.item.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CommentOutputDto {
    private Long id;
    private String text;
    private ItemOutputDto item;
    private String authorName;
    private LocalDateTime created;
}
