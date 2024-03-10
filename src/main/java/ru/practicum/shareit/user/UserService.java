package ru.practicum.shareit.user;


import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserService {

    UserDto add(UserDto userDto);

    UserDto update(UserDto userDto);

    void delete(int userId);

    UserDto get(int userId);

    List<UserDto> getAll();


}
