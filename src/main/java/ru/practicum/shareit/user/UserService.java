package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.NewUserDto;
import ru.practicum.shareit.user.dto.UpdUserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserService {

    User add(NewUserDto newUserDto);

    User update(UpdUserDto updUserDto, int userId);

    void delete(int userId);

    User get(int userId);

    List<User> getAll();


}
