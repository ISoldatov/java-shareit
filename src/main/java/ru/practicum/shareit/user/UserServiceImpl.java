package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.NewUserDto;
import ru.practicum.shareit.user.dto.UpdUserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.util.exception.FoundException;

import java.util.List;

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
    public User add(NewUserDto newUserDto) {
        log.info("UserService: add({})", newUserDto);
        checkFound(userRepository.getByEmail(newUserDto.getEmail()), newUserDto.getEmail());
        return userRepository.save(toUser(newUserDto));
    }

    @Override
    public User update(UpdUserDto updUserDto, int userId) {
        log.info("UserService: update({},{})", updUserDto, userId);
        User myUser = get(userId);
        if (updUserDto.getName() != null) {
            myUser.setName(updUserDto.getName());
        }
        if (updUserDto.getEmail() != null) {
            User userWithSameMail = userRepository.getByEmail(updUserDto.getEmail());
            if (userWithSameMail != null && !userWithSameMail.getId().equals(userId)) {
                throw new FoundException(String.format("Email %s уже существует.", updUserDto.getEmail()));
            }
            myUser.setEmail(updUserDto.getEmail());
        }
        return userRepository.update(myUser);
    }

    @Override
    public void delete(int userId) {
        log.info("UserService: delete({})", userId);
        checkNotFoundWithId(userRepository.delete(userId), userId, "user");
    }

    @Override
    public User get(int userId) {
        log.info("UserService: get({})", userId);
        return checkNotFoundWithId(userRepository.get(userId), userId, "user");
    }

    @Override
    public List<User> getAll() {
        log.info("UserService: addAll()");
        return userRepository.getAll();
    }
}
