package ru.practicum.shareit.user.dto;

import ru.practicum.shareit.user.model.User;

public final class UserMapper {

    public static UserDto toUserDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }

    public static User toUser(NewUserDto newUserDto) {
        return User.builder()
                .id(null)
                .name(newUserDto.getName())
                .email(newUserDto.getEmail())
                .build();
    }
}
