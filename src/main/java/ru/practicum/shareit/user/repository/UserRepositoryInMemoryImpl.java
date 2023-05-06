package ru.practicum.shareit.user.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;


@Component
@Slf4j
public class UserRepositoryInMemoryImpl implements UserRepository {

    private final Map<Integer, User> users = new HashMap<>();
    private int userId = 0;

    @Override
    public User create(User user) {
        log.debug("Creating user: {}", user);
        user.setId(++userId);
        users.put(user.getId(), user);
        return users.get(user.getId());
    }

    @Override
    public User getById(int id) {
        log.debug("Getting User by ID: {}", id);
        return users.get(id);
    }

    @Override
    public Collection<User> getAll() {
        log.debug("Getting all users");
        return new ArrayList<>(users.values());
    }

    @Override
    public User update(User user) {
        log.debug("Updating user with id: {}, user: {}", user.getId(), user);
        users.put(user.getId(), user);
        return users.get(user.getId());
    }

    @Override
    public void delete(int id) {
        log.debug("Removing User with id : {}", id);
        users.remove(id);
    }
}
