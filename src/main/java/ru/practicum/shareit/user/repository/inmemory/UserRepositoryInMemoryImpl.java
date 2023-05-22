package ru.practicum.shareit.user.repository.inmemory;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.EmailIsAlreadyRegisteredException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.*;


@Component
@Slf4j
public class UserRepositoryInMemoryImpl implements UserRepository {

    private final Map<Long, User> users = new HashMap<>();
    private final Set<String> uniqueEmailsSet = new HashSet<>();
    private int userId = 0;

    @Override
    public User create(User user) {
        log.debug("Creating user: {}", user);
        user.setId(++userId);
        if (!uniqueEmailsSet.add(user.getEmail())) {
            --userId;
            throw new EmailIsAlreadyRegisteredException("User with this email is already exists!");
        }
        users.put(user.getId(), user);
        return users.get(user.getId());
    }

    @Override
    public User getById(long id) {
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
        if (!user.getEmail().equals(users.get(user.getId()).getEmail())) {
            if (uniqueEmailsSet.add(user.getEmail())) {
                uniqueEmailsSet.remove(users.get(user.getId()).getEmail());
            } else {
                throw new EmailIsAlreadyRegisteredException("User with this email is already exists!");
            }
        }
        log.debug("Updating user with id: {}, user: {}", user.getId(), user);

        users.put(user.getId(), user);
        return users.get(user.getId());
    }

    @Override
    public void delete(long id) {
        log.debug("Removing User with id : {}", id);
        uniqueEmailsSet.remove(users.get(id).getEmail());
        users.remove(id);
    }
}
