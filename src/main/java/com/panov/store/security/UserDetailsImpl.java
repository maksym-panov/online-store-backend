package com.panov.store.security;

import com.panov.store.model.User;
import com.panov.store.utils.Access;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

/**
 * This implementation of {@link UserDetails} interface is used during <br>
 * authorisation and authentication of {@link User}.
 *
 * @author Maksym Panov
 * @version 1.0
 */
public class UserDetailsImpl implements UserDetails {
    private final User user;

    public UserDetailsImpl(User user) {
        this.user = user;
    }

    /**
     * Provides authorities for each {@link User} including {@link Access} <br>
     * authority and an identity of this {@link User}, which is used when <br>
     * you need to change {@link User} information.
     *
     * @return {@link java.util.Collections} of {@link User} authorities
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(() -> user.getAccess().toString(),
                () -> user.getUserId().toString());
    }

    /**
     * Returns an encoded password of {@link User}.
     *
     * @return encoded password of the {@link User}
     */
    @Override
    public String getPassword() {
        return user.getHashPassword();
    }

    /**
     * Returns a username of {@link User} (phone number).
     *
     * @return phone number of the {@link User}
     */
    @Override
    public String getUsername() {
        return user.getPersonalInfo().getPhoneNumber();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
