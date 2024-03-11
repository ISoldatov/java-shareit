package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.util.exception.FoundException;
import ru.practicum.shareit.util.exception.NotFoundException;
import ru.practicum.shareit.util.exception.ValidationException;

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
//        checkEmail(userDto.getEmail(), userDto.getId());
        return UserMapper.mapToUserDto(userRepository.save(UserMapper.mapToUser(userDto)));
    }

    @Override
    public UserDto update(UserDto userDto) {
        log.info("UserService: update({})", userDto);
        User userInBase = userRepository.findById(userDto.getId())
                .orElseThrow(() -> new ValidationException(String.format("User c id=%d не найден.", userDto.getId())));
        if (userDto.getName() != null) {
            userInBase.setName(userDto.getName());
        }
        String userDtoEmail = userDto.getEmail();
        if (userDtoEmail != null) {
            checkEmail(userDtoEmail, userDto.getId());
            userInBase.setEmail(userDtoEmail);
        }
        return mapToUserDto(userRepository.save(userInBase));
    }

    @Override
    public void delete(int userId) {
        log.info("UserService: delete({})", userId);
        userRepository.deleteById(userId);
    }

    @Override
    public UserDto get(int userId) {
        log.info("UserService: get({})", userId);
//        return mapToUserDto(userRepository.getReferenceById(userId));
        return mapToUserDto(userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("User c id=%d не найден.", userId))));
    }

    @Override
    public List<UserDto> getAll() {
        log.info("UserService: getAll()");
        return userRepository.findAll().stream()
                .map(UserMapper::mapToUserDto)
                .collect(Collectors.toList());
    }

    private void checkEmail(String email, Integer userId) {
        User userWithSameMail = userRepository.findByEmailContainingIgnoreCase(email);
        if (userWithSameMail != null && !userWithSameMail.getId().equals(userId)) {
            throw new FoundException(String.format("Email %s уже существует.", email));
        }
    }
}
