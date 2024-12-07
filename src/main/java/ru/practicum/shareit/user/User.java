package ru.practicum.shareit.user;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.validation.CreateUserGroup;
import ru.practicum.shareit.validation.UpdateUserGroup;

@Entity
@Table(name = "users", schema = "public")
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    @NotBlank(groups = CreateUserGroup.class)
    private String name;

    @Column(name = "email")
    @Email(groups = {CreateUserGroup.class, UpdateUserGroup.class})
    @NotBlank(groups = CreateUserGroup.class)
    private String email;
}
