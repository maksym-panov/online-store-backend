package com.panov.store.controllers;

import com.panov.store.model.User;
import com.panov.store.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService service;

    @Autowired
    public UserController(UserService service) {
        this.service = service;
    }

    @GetMapping
    public List<User> userRange() {
        return service.getAllUserList();
    }

    @GetMapping("/{id}")
    public User specificUser(@PathVariable("id") Integer id) {
        return service.getById(id);
    }
}
