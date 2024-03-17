package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.Valid;
import java.util.List;

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
    public UserDto add(@Valid @RequestBody UserDto userDto) {
        log.info("UserController: POST /users, user={} ", userDto);
        return userService.add(userDto);
    }

    @PatchMapping("/{userId}")
    public UserDto update(@RequestBody UserDto userDto, @PathVariable int userId) {
        log.info("UserController: PATCH /users/{}, userDto={} ", userId, userDto);
        userDto.setId(userId);
        return userService.update(userDto);
    }

    @DeleteMapping("/{userId}")
    public void delete(@PathVariable int userId) {
        log.info("UserController: DELETE /users/{}", userId);
        userService.delete(userId);
    }

    @GetMapping("/{userId}")
    public UserDto get(@PathVariable int userId) {
        log.info("UserController: GET /users/{}", userId);
        return userService.get(userId);
    }

    @GetMapping
    public List<UserDto> getAll() {
        log.info("UserController: GET /users/");
        return userService.getAll();
    }
}
