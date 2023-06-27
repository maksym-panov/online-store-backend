package com.panov.store.dto;

import com.panov.store.model.Address;
import com.panov.store.model.User;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;

/**
 * Data transfer class that is used to transfer data, needed to register new {@link User} entities.
 *
 * @author Maksym Panov
 * @version 1.0
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RegistrationForm {
    private User.PersonalInfo personalInfo;

    private Address address;

    @Size(min = 8, message = "Your password is too short")
    private String password;

    /**
     * Converts current {@link RegistrationForm} object to an instance of {@link User}.
     *
     * @return a {@link User} instance
     */
    public User toModel() {
        var u = new User();
        u.setHashPassword(password);
        u.setPersonalInfo(personalInfo);
        u.setAddress(address == null ? new Address() : address);
        u.setOrders(new ArrayList<>());
        return u;
    }
}
