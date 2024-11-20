package ru.practicum.shareit.user;

import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class InMemoryUserRepository implements UserRepository {
    private final Map<Long, User> users = new HashMap<>();
    private Long idCounter = 0L;

    @Override
    public List<User> getAll() {
        return users.values().stream().toList();
    }

    @Override
    public User getById(Long id) {
        return users.get(id);
    }

    @Override
    public User create(User user) {
        user.setId(++idCounter);
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User update(Long id, User user) {
        User currentUser = users.get(id);
        currentUser.setName(user.getName());
        currentUser.setEmail(user.getEmail());
        users.put(id, currentUser);
        return currentUser;
    }

    @Override
    public void deleteById(Long id) {
        users.remove(id);
    }

}
