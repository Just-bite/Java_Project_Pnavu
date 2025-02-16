package com.example.restservice.repository;

import com.example.restservice.model.User;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Repository
public class UserRepository {

    private final List<User> userList;

    public UserRepository() {
        userList = new ArrayList<>();

        User user1 = new User(1, "Peter", "Entschuldigung");
        User user2 = new User(2, "Helena", "123");
        User user3 = new User(3, "Bob", "qwerty");
        User user4 = new User(4, "John", "Sadccttyy88jjrrnn77");
        User user5 = new User(5, "Ann", "123Q321Q");

        userList.addAll(Arrays.asList(user1, user2, user3, user4, user5));
    }

    public List<User> getAllUsers() {
        return userList;
    }

    public Optional<User> getUserById(Integer id) {
        return userList.stream()
                .filter(user -> user.getId()==id)
                .findFirst();
    }
}

