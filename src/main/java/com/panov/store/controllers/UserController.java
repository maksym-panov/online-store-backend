package com.panov.store.controllers;

import com.panov.store.model.User;
import com.panov.store.services.UserService;
import com.panov.store.utils.ListUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
    public List<User> userRange(@RequestBody(required = false) String naturalId,
                                @RequestParam(name = "quantity", required = false) Integer quantity,
                                @RequestParam(name = "offset", required = false) Integer offset) {
        if (naturalId == null)
            return ListUtils.makeCut(service.getAllUserList(), quantity, offset);
        return service.getByNaturalId(naturalId);
    }

    @GetMapping("/{id}")
    public User specificUser(@PathVariable("id") Integer id) {
        return service.getById(id);
    }
}
