package com.panov.store.controllers;

import com.panov.store.jwt.JwtService;
import com.panov.store.model.User;
import com.panov.store.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * This controller helps client check if its auth token is not expired yet
 *
 * @author Maksym Panov
 * @version 1.0
 */
@RestController
@RequestMapping("/api/v2/ping")
@RequiredArgsConstructor
public class PingController {
    private final JwtService jwtService;
    private final UserService userService;

    @PostMapping("/{id}")
    public ResponseEntity<Boolean> checkAccess(
            @PathVariable("id") Integer id,
            @RequestBody String jwt
    ) {
        User user = userService.getById(id);
        return new ResponseEntity<>(jwtService.isTokenValid(jwt, user), HttpStatus.OK);
    }
}
