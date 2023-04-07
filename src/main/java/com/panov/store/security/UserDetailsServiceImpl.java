package com.panov.store.security;

import com.panov.store.model.User;
import com.panov.store.services.UserService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserService userService;

    public UserDetailsServiceImpl(UserService userService) {
        this.userService = userService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        boolean strict = true;
        List<User> userList = userService.getByNaturalId(username, strict);

        if (userList.size() == 0)
            throw new UsernameNotFoundException("Couldn't find user with username " + username);

        return new UserDetailsImpl(userList.get(0));
    }


}
