package ru.practicum.shareit.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
//    User save(User user);

//    User update(User user);

//    boolean delete(int id);

//    User get(int id);

    User findByEmailContainingIgnoreCase(String email);

//    List<User> getAll();
}
