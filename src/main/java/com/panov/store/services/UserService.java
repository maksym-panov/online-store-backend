package com.panov.store.services;

import com.panov.store.dao.UserRepository;
import com.panov.store.exceptions.ResourceNotCreatedException;
import com.panov.store.exceptions.ResourceNotFoundException;
import com.panov.store.exceptions.ResourceNotUpdatedException;
import com.panov.store.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import com.panov.store.dao.DAO;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service-layer class that processes {@link User} entities.
 *
 * @author Maksym Panov
 * @version 1.0
 * @see DAO
 * @see UserRepository
 */
@Service
public class UserService {
    private final DAO<User> repository;

    @Autowired
    public UserService(DAO<User> repository) {
        this.repository = repository;
    }

    /**
     * Uses {@link DAO} implementation to retrieve list of all existing {@link User} entities. <br><br>
     * Only {@link User} with {@code Access.ADMINISTRATOR} or {@code Access.MANAGER} authority
     * can invoke this method. <br><br>
     * Re-throws a {@link ResourceNotFoundException} if {@link DAO} object throws an exception.
     *
     * @return a {@link List} of {@link User} objects
     */
    @PreAuthorize("hasAuthority('ADMINISTRATOR') or hasAuthority('MANAGER')")
    public List<User> getAllUserList() {
        try {
            var list = repository.getAll();
            if (list == null)
                return Collections.emptyList();
            return list;
        } catch(Exception e) {
            e.printStackTrace();
            throw new ResourceNotFoundException("Could not find users");
        }
    }

    /**
     * Uses {@link DAO} implementation to retrieve a {@link User} entity by specified identity. <br><br>
     * Only {@link User} with {@code Access.ADMINISTRATOR} or {@code Access.MANAGER} authority or the
     * {@link User} that is the owner of provided identity, can invoke this method. <br><br>
     * Re-throws a {@link ResourceNotFoundException} if {@link DAO} object throws an exception or there is
     * no such {@link User} object.
     *
     * @param id an identity of the sought {@link User}
     * @return a {@link User} object with specified identity
     */
    @PreAuthorize("hasAuthority('ADMINISTRATOR') or hasAuthority('MANAGER') or hasAuthority(#id.toString)")
    public User getById(Integer id) {
        return repository.get(id).orElseThrow(() -> new ResourceNotFoundException("Could not find this user"));
    }

    /**
     * Searches for {@link User} objects using {@link DAO} implementation by specified
     * phone number or email. <br><br>
     * Re-throws a {@link ResourceNotFoundException} if {@link DAO} object throws an exception.
     *
     * @param naturalId a phone number or email of the sought user
     * @return a list of {@link User} objects whose have specified {@code naturalId}
     */
    public List<User> getByNaturalId(String naturalId) {
        try {
            boolean strict = true;
            var users = repository.getByColumn(naturalId, strict);
            if (users == null)
                return Collections.emptyList();
            return users;
        } catch(Exception e) {
            e.printStackTrace();
            throw new ResourceNotFoundException("Could not find users");
        }
    }

    /**
     * Uses {@link DAO} implementation to register and save new {@link User} in the data storage. <br><br>
     * Re-throws a {@link ResourceNotCreatedException} if {@link DAO} object throws an exception.
     *
     * @param user {@link User} that should be registered
     * @return an identity of registered {@link User}
     */
    public Integer registerUser(User user) {
        var matches = thisNaturalIdExists(user);
        if (matches.size() != 0)
            throw new ResourceNotCreatedException(matches);

        Integer id = null;

        try {
            id = repository.insert(user);
        } catch(Exception e) {
            e.printStackTrace();
        }

        if (id == null)
            throw new ResourceNotCreatedException("Could not create this user");

        return id;
    }

    /**
     * Uses {@link DAO} implementation to change information of {@link User}. <br><br>
     * Only {@link User} with {@code Access.ADMINISTRATOR} or {@code Access.MANAGER} authority or the
     * {@link User} that is the owner of provided identity, can invoke this method. <br><br>
     * Re-throws a {@link ResourceNotUpdatedException} if {@link DAO} object throws an exception.
     *
     * @param user an object that contains new data and identity of {@link User}
     *             that should be updated
     * @return an identity of updated {@link User}
     */
    @PreAuthorize(
            "hasAuthority('ADMINISTRATOR') or " +
            "hasAuthority('MANAGER') or " +
            "hasAuthority(#user.getUserId().toString())"
    )
    public Integer changeUser(User user) {
        var matches = thisNaturalIdExists(user);
        if (matches.size() != 0)
            throw new ResourceNotCreatedException(matches);

        Integer id = null;

        try {
            id = repository.update(user);
        } catch(Exception e) {
            e.printStackTrace();
        }

        if (id == null)
            throw new ResourceNotCreatedException("Could not change this user");

        return id;
    }

    /**
     * Checks if the phone number or the email of object is already used in existing {@link User} object.
     *
     * @param user an object to check
     * @return a {@link Map} that contains decision about uniqueness of the phone number
     * and the email of specified {@link User}
     */
    private Map<String, String> thisNaturalIdExists(User user) {
        Map<String, String> matches = new HashMap<>();

        try {
            var phoneNumberMatch = getByNaturalId(user.getPersonalInfo().getPhoneNumber());
            if (phoneNumberMatch.size() != 0 && !user.getUserId().equals(phoneNumberMatch.get(0).getUserId()))
                matches.put("phoneNumber", "User with this phone number already exists");
        } catch(Exception ignored) {}

        try {
            var emailMatch = getByNaturalId(user.getPersonalInfo().getEmail());
            if (emailMatch.size() != 0 && !user.getUserId().equals(emailMatch.get(0).getUserId()))
                matches.put("email", "User with this email already exists");
        } catch(Exception ignored) {}

        return matches;
    }
}
