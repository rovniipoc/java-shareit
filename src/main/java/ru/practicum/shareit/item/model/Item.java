package ru.practicum.shareit.item.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.validation.CreateGroup;

@Entity
@Table(name = "item", schema = "public")
@Data
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    @NotBlank(groups = CreateGroup.class)
    private String name;

    @Column(name = "description")
    @NotBlank(groups = CreateGroup.class)
    private String description;

    @Column(name = "available")
    @NotNull(groups = CreateGroup.class)
    private Boolean available;

    @Column(name = "owner")
    @NotNull(groups = CreateGroup.class)
    private Long owner;

    @OneToOne
    @JoinColumn(name = "request_id")
    private ItemRequest request;
}
