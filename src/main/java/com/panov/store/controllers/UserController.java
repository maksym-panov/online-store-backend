package com.panov.store.controllers;

import com.panov.store.dto.RegistrationForm;
import com.panov.store.dto.UserDTO;
import com.panov.store.exceptions.ResourceNotCreatedException;
import com.panov.store.exceptions.ResourceNotUpdatedException;
import com.panov.store.model.User;
import com.panov.store.services.UserService;
import com.panov.store.utils.ListUtils;
import com.panov.store.utils.PasswordEncoder;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
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
        return service.getByNaturalId(naturalId, true);
    }

    @GetMapping("/{id}")
    public User specificUser(@PathVariable("id") Integer id) {
        return service.getById(id);
    }

    @PostMapping
    public Integer createUser(@RequestBody @Valid RegistrationForm form,
                              BindingResult bindingResult) {
        if (bindingResult.hasErrors())
            throw new ResourceNotCreatedException(bindingResult);

        form.setPassword(PasswordEncoder.encode(form.getPassword()));

        return service.createUser(form.toModel());
    }

    @PatchMapping("/{id}")
    public Integer changeUser(@RequestBody @Valid UserDTO userDTO,
                              @PathVariable("id") Integer id,
                              BindingResult bindingResult) {
        if (bindingResult.hasErrors())
            throw new ResourceNotUpdatedException(bindingResult);

        userDTO.setUserId(id);

        return service.changeUser(userDTO.toModel());
    }
}
