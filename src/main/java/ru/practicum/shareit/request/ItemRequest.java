package ru.practicum.shareit.request;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "request", schema = "public")
public class ItemRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "description")
    @NotBlank
    private String description;

    @ManyToOne
    @JoinColumn(name = "requestor")
    private User requestor;

    @Column(name = "created")
    @NotNull
    private LocalDateTime created = LocalDateTime.now();
}
