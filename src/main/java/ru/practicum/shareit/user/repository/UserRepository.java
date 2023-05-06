package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.model.User;

import java.util.Collection;

public interface UserRepository {

    User create(User user);

    User getById(int id);

    Collection<User> getAll();

    User update(User user);

    void delete(int id);
}
