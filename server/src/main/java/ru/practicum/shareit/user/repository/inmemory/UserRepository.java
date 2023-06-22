package ru.practicum.shareit.user.repository.inmemory;

import ru.practicum.shareit.user.model.User;

import java.util.Collection;

public interface UserRepository {

    User create(User user);

    User getById(long id);

    Collection<User> getAll();

    User update(User user);

    void delete(long id);
}
