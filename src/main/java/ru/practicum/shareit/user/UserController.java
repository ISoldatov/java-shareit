package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.NewUserDto;
import ru.practicum.shareit.user.dto.UpdUserDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.shareit.user.dto.UserMapper.*;

@Slf4j
@RestController
@RequestMapping(path = "/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public UserDto add(@Valid @RequestBody NewUserDto newUserDto) {
        log.info("UserController: POST /users, user={} ", newUserDto);
        return toUserDto(userService.add(newUserDto));
    }

    @PatchMapping("/{userId}")
    public UserDto update(@Valid @RequestBody UpdUserDto updUserDto, @PathVariable int userId) {
        log.info("UserController: PATCH /users/{}, userDto={} ", userId, updUserDto);
        return toUserDto(userService.update(updUserDto, userId));
    }

    @DeleteMapping("/{userId}")
    public void delete(@Valid @PathVariable int userId) {
        log.info("UserController: DELETE /users/{}", userId);
        userService.delete(userId);
    }

    @GetMapping("/{userId}")
    public UserDto get(@Valid @PathVariable int userId) {
        log.info("UserController: GET /users/{}", userId);
        return toUserDto(userService.get(userId));
    }

    @GetMapping
    public List<UserDto> getAll() {
        log.info("UserController: GET /users/");
        return userService.getAll().stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }
}
