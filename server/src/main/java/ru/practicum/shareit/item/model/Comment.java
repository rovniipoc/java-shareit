package ru.practicum.shareit.item.model;

import jakarta.persistence.*;
import lombok.Data;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

@Entity
@Table(name = "comment", schema = "public")
@Data
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "text")
    private String text;

    @ManyToOne
    @JoinColumn(name = "item_id")
    private Item item;

    @ManyToOne
    @JoinColumn(name = "author_id")
    private User author;

    @Column(name = "created")
    private LocalDateTime created = LocalDateTime.now();
}
