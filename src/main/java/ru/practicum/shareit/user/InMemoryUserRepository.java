package ru.practicum.shareit.user;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.ValidationException;

import java.util.*;

@Repository
public class InMemoryUserRepository implements UserRepository {
    private final Map<Long, User> users = new HashMap<>();
    private final Set<String> emailUniqSet = new HashSet<>();
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
        String email = user.getEmail();
        if (emailUniqSet.contains(email)) {
            throw new ValidationException("Пользователь с email = " + user.getEmail() + " уже существует");
        }
        user.setId(++idCounter);
        users.put(user.getId(), user);
        emailUniqSet.add(email);
        return user;
    }

    @Override
    public User update(User user) {
        User currentUser = users.get(user.getId());
        currentUser.setName(user.getName());
        String newEmail = user.getEmail();
        String oldEmail = currentUser.getEmail();
        if (!newEmail.equals(oldEmail) && emailUniqSet.contains(newEmail)) {
            throw new ValidationException("Пользователь с email = " + user.getEmail() + " уже существует");
        }
        currentUser.setEmail(newEmail);
        emailUniqSet.add(newEmail);
        emailUniqSet.remove(oldEmail);
        users.put(user.getId(), currentUser);
        return currentUser;
    }

    @Override
    public void deleteById(Long id) {
        emailUniqSet.remove(users.get(id).getEmail());
        users.remove(id);
    }

}
