package com.example.restservice.controller;

import com.example.restservice.BadRequestException;
import com.example.restservice.model.User;
import com.example.restservice.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@CustomExceptionHandler
@Tag(name = "Контроллер пользователей", description
          = "Позволяет получать, создавать и удалять пользователей")
public class UserController {
    private final UserService userService;

    @GetMapping
    @Operation(
            summary = "Выводит пользователей",
            description = "Выводит всех пользователей и информацию о них"
    )
    public List<User> getAllUsers() {
        List<User> users = userService.getAllUsers();
        if (users.isEmpty()) {
            throw new BadRequestException("Список пользователей пуст");
        }
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Выводит пользователя",
            description = "Выводит пользователя по его уникальному идентификатору"
    )
    public User getUserById(@PathVariable long id) {
        User user = userService.getUserById(id);
        if (user == null) {
            throw new BadRequestException("Пользователь с id " + id + " не найден");
        }
        return user;
    }

    @PostMapping
    @Operation(
            summary = "Создает пользователей"
    )
    public List<User> createUsers(@RequestBody List<User> users) {
        if (users == null || users.isEmpty()) {
            throw new BadRequestException("Список пользователей не может быть пустым");
        }
        return userService.createUsers(users);
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Удаляет пользователя",
            description = "Удаляет пользователя по его id"
    )
    public void deleteUser(@PathVariable Long id) {
        User user = userService.getUserById(id);
        if (user == null) {
            throw new BadRequestException("Пользователь с id " + id + " не найден");
        }
        userService.deleteUser(id);
    }
}
