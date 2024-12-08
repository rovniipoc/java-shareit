package ru.practicum.shareit.item.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.validation.CreateItemGroup;

@Entity
@Table(name = "item", schema = "public")
@Data
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    @NotBlank(groups = CreateItemGroup.class)
    private String name;

    @Column(name = "description")
    @NotBlank(groups = CreateItemGroup.class)
    private String description;

    @Column(name = "available")
    @NotNull(groups = CreateItemGroup.class)
    private Boolean available;

    @Column(name = "owner")
    private Long owner;

    @OneToOne
    @JoinColumn(name = "request_id")
    private ItemRequest request;
}
