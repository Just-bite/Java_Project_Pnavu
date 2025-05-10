package com.example.restservice.controller;

import com.example.restservice.exception.BadRequestException;
import com.example.restservice.exception.CustomExceptionHandler;
import com.example.restservice.exception.NotFoundException;
import com.example.restservice.model.User;
import com.example.restservice.model.UserDto;
import com.example.restservice.model.UserUpdateRequest;
import com.example.restservice.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Tag(name = "User Controller", description
        = "Allows retrieving, creating, and deleting users")
@CustomExceptionHandler
public class UserController {
    private final UserService userService;

    @GetMapping
    @Operation(
            summary = "Get all users",
            description = "Retrieves all users and their information"
    )
    public List<UserDto> getAllUsers() {
        List<UserDto> users = userService.getAllUsers();
        if (users.isEmpty()) {
            throw new NotFoundException("The user list is empty");
        }
        return users;
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Get user by id",
            description = "Retrieves a user by their unique identifier"
    )
    public User getUserById(@PathVariable long id) {
        User user = userService.getUserById(id);
        if (user == null) {
            throw new NotFoundException("User with id " + id + " not found");
        }
        return user;
    }

    @PostMapping
    @Operation(
            summary = "Create users"
    )
    public List<User> createUsers(@RequestBody List<User> users) {
        if (users == null || users.isEmpty()) {
            throw new BadRequestException("The user list cannot be empty");
        }
        return userService.createUsers(users);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update user")
    public void updateUser(
            @PathVariable long id,
            @RequestBody UserUpdateRequest updateRequest) {

        userService.updateUser(id, updateRequest);
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Delete a user",
            description = "Deletes a user by their id"
    )
    public void deleteUser(@PathVariable Long id) {
        User user = userService.getUserById(id);
        if (user == null) {
            throw new NotFoundException("User with id " + id + " not found");
        }
        userService.deleteUser(id);
    }
}