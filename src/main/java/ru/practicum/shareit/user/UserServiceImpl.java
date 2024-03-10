package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.util.exception.FoundException;

import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.shareit.util.ValidationUtil.*;
import static ru.practicum.shareit.user.dto.UserMapper.*;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDto add(UserDto userDto) {
        log.info("UserService: add({})", userDto);
        checkFound(userRepository.getByEmail(userDto.getEmail()), userDto.getEmail());
        return UserMapper.mapToUserDto(userRepository.save(UserMapper.mapToUser(userDto)));
    }

    @Override
    public UserDto update(UserDto userDto) {
        log.info("UserService: update({})", userDto);
        User userInBase = userRepository.get(userDto.getId());
        if (userDto.getName() != null) {
            userInBase.setName(userDto.getName());
        }
        String userDtoEmail = userDto.getEmail();
        if (userDtoEmail != null) {
            User userWithSameMail = userRepository.getByEmail(userDtoEmail);
            if (userWithSameMail != null && !userWithSameMail.getId().equals(userDto.getId())) {
                throw new FoundException(String.format("Email %s уже существует.", userDtoEmail));
            }
            userInBase.setEmail(userDtoEmail);
        }
        return mapToUserDto(userRepository.update(userInBase));
    }

    @Override
    public void delete(int userId) {
        log.info("UserService: delete({})", userId);
        checkNotFoundWithId(userRepository.delete(userId), userId, "user");
    }

    @Override
    public UserDto get(int userId) {
        log.info("UserService: get({})", userId);
        return checkNotFoundWithId(mapToUserDto(userRepository.get(userId)), userId, "user");
    }

    @Override
    public List<UserDto> getAll() {
        log.info("UserService: getAll()");
        return userRepository.getAll().stream()
                .map(UserMapper::mapToUserDto)
                .collect(Collectors.toList());
    }
}
