package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dto.UserInputDto;
import ru.practicum.shareit.user.dto.UserOutputDto;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@Transactional
public class UserServiceTests {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    private UserInputDto userInputDto;
    private UserOutputDto userOutputDto;
    private Long userId;

    @BeforeEach
    void setUp() {
        userInputDto = new UserInputDto();
        userInputDto.setName("Test User");
        userInputDto.setEmail("test@example.com");

        userOutputDto = userService.create(userInputDto);
        userId = userOutputDto.getId();
    }

    @Test
    void testGetAllUsers() {
        List<UserOutputDto> users = userService.getAll();
        assertFalse(users.isEmpty());
        assertEquals(1, users.size());
        assertEquals(userOutputDto.getId(), users.get(0).getId());
    }

    @Test
    void testGetUserById() {
        UserOutputDto user = userService.getById(userId);
        assertNotNull(user);
        assertEquals(userOutputDto.getId(), user.getId());
        assertEquals(userOutputDto.getName(), user.getName());
        assertEquals(userOutputDto.getEmail(), user.getEmail());
    }

    @Test
    void testCreateUser() {
        UserInputDto newUserInputDto = new UserInputDto();
        newUserInputDto.setName("New User");
        newUserInputDto.setEmail("newuser@example.com");

        UserOutputDto newUserOutputDto = userService.create(newUserInputDto);
        assertNotNull(newUserOutputDto);
        assertEquals(newUserInputDto.getName(), newUserOutputDto.getName());
        assertEquals(newUserInputDto.getEmail(), newUserOutputDto.getEmail());
    }

    @Test
    void testUpdateUser() {
        UserInputDto updatedUserInputDto = new UserInputDto();
        updatedUserInputDto.setName("Updated User");
        updatedUserInputDto.setEmail("updateduser@example.com");

        UserOutputDto updatedUserOutputDto = userService.update(userId, updatedUserInputDto);
        assertNotNull(updatedUserOutputDto);
        assertEquals(updatedUserInputDto.getName(), updatedUserOutputDto.getName());
        assertEquals(updatedUserInputDto.getEmail(), updatedUserOutputDto.getEmail());
    }

    @Test
    void testDeleteUser() {
        userService.deleteById(userId);
        assertThrows(NotFoundException.class, () -> userService.getById(userId));
    }

    @Test
    void testCreateUserWithDuplicateEmail() {
        UserInputDto duplicateUserInputDto = new UserInputDto();
        duplicateUserInputDto.setName("Duplicate User");
        duplicateUserInputDto.setEmail("test@example.com");

        assertThrows(ValidationException.class, () -> userService.create(duplicateUserInputDto));
    }

    @Test
    void testUpdateUserWithDuplicateEmail() {
        UserInputDto duplicateUserInputDto = new UserInputDto();
        duplicateUserInputDto.setName("Duplicate User");
        duplicateUserInputDto.setEmail("test@example.com");

        UserInputDto newUserInputDto = new UserInputDto();
        newUserInputDto.setName("New User");
        newUserInputDto.setEmail("newuser@example.com");
        UserOutputDto newUserOutputDto = userService.create(newUserInputDto);

        duplicateUserInputDto.setEmail("newuser@example.com");
        assertThrows(ValidationException.class, () -> userService.update(newUserOutputDto.getId(), duplicateUserInputDto));
    }
}
