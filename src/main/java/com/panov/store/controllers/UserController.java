package com.panov.store.controllers;

import com.panov.store.dto.RegistrationForm;
import com.panov.store.dto.UserDTO;
import com.panov.store.exceptions.ResourceNotCreatedException;
import com.panov.store.exceptions.ResourceNotUpdatedException;
import com.panov.store.model.User;
import com.panov.store.services.UserService;
import com.panov.store.utils.Access;
import com.panov.store.utils.ListUtils;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService service;
    private final PasswordEncoder encoder;

    @Autowired
    public UserController(UserService service, PasswordEncoder encoder) {
        this.service = service;
        this.encoder = encoder;
    }

    @GetMapping
    public List<UserDTO> userRange(@RequestParam(name = "quantity", required = false) Integer quantity,
                                @RequestParam(name = "offset", required = false) Integer offset) {
        List<UserDTO> users = service.getAllUserList()
                .stream()
                .map(UserDTO::of)
                .toList();

        return ListUtils.makeCut(users, quantity, offset);
    }

    @GetMapping("/{id}")
    public UserDTO specificUser(@PathVariable("id") Integer id) {
        return UserDTO.of(service.getById(id));
    }

    @PostMapping
    public Integer createUser(@RequestBody @Valid RegistrationForm form,
                              BindingResult bindingResult) {
        if (bindingResult.hasErrors())
            throw new ResourceNotCreatedException(bindingResult);

        User userToCreate = form.toModel();

        userToCreate.setAccess(Access.USER);
        userToCreate.setHashPassword(encoder.encode(form.getPassword()));

        return service.createUser(userToCreate);
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

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMINISTRATOR')")
    public Integer changeUserAccess(@PathVariable("id") Integer id,
                                    @RequestBody String access) {
        User user = new User();
        user.setUserId(id);

        try {
            user.setAccess(Access.valueOf(access));
        } catch (Exception e) {
            throw new ResourceNotUpdatedException("Could not update access level of this user");
        }

        return service.changeUser(user);
    }
}
