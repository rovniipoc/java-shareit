package ru.practicum.shareit.user.dto;

import ru.practicum.shareit.user.User;

import java.util.ArrayList;
import java.util.List;

public class UserMapper {
    public static UserOutputDto toUserOutputDto(User user) {
        UserOutputDto userOutputDto = new UserOutputDto();
        userOutputDto.setId(user.getId());
        userOutputDto.setName(user.getName());
        userOutputDto.setEmail(user.getEmail());
        return userOutputDto;
    }

    public static List<UserOutputDto> toUserOutputDto(Iterable<User> users) {
        List<UserOutputDto> result = new ArrayList<>();
        for (User user : users) {
            result.add(toUserOutputDto(user));
        }
        return result;
    }

    public static User toUser(UserInputDto userInputDto) {
        User user = new User();
        user.setName(userInputDto.getName());
        user.setEmail(userInputDto.getEmail());
        return user;
    }

}
