package com.panov.store.dto;

import com.panov.store.model.Address;
import com.panov.store.model.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;

/**
 * Data transfer abstraction class, designed for communication between <br>
 * frontend and backend represented by this application. Wraps {@link User} objects.
 *
 * @author Maksym Panov
 * @version 1.0
 * @see User
 * @see Address
 * @see User.PersonalInfo
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private Integer userId;

    private User.PersonalInfo personalInfo;

    private Address address;

    /**
     * Static factory that converts specified {@link User} object <br>
     * to a new instance of {@link UserDTO}
     *
     * @param u a {@link User} object to convert
     * @return a transfer-safe {@link UserDTO} abstraction
     */
    public static UserDTO of(User u) {
        if (u == null)
            return null;
        return new UserDTO(
            u.getUserId(),
            u.getPersonalInfo(),
            u.getAddress()
        );
    }

    /**
     * Converts current {@link UserDTO} object to an instance of {@link User}.
     *
     * @return a {@link User} instance
     */
    public User toModel() {
        var u = new User();
        u.setUserId(userId);
        u.setPersonalInfo(personalInfo);
        u.setAddress(address);
        u.setOrders(new ArrayList<>());
        return u;
    }
}
