package com.panov.store.security;

import com.panov.store.model.User;
import com.panov.store.services.UserService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * This implementation of {@link UserDetailsService} is used during authentication of the {@link User}.
 *
 * @author Maksym Panov
 * @version 1.0
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserService userService;

    public UserDetailsServiceImpl(UserService userService) {
        this.userService = userService;
    }

    /**
     * Retrieves specific {@link User} by its phone number (a.k.a. login/username) during authentication process.
     *
     * @param phoneNumber the username identifying the user, whose data is required
     * @return {@link User} wrapped in {@link UserDetailsImpl} object
     * @throws UsernameNotFoundException if {@link User} with specified phone number is not found.
     */
    @Override
    public UserDetails loadUserByUsername(String phoneNumber) throws UsernameNotFoundException {
        List<User> userList = userService.getByNaturalId(phoneNumber);

        if (userList.size() == 0)
            throw new UsernameNotFoundException("Couldn't find user with phone number " + phoneNumber);

        return new UserDetailsImpl(userList.get(0));
    }


}
