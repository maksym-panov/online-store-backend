package com.panov.store.services;

import com.panov.store.exceptions.ResourceNotCreatedException;
import com.panov.store.exceptions.ResourceNotFoundException;
import com.panov.store.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.panov.store.dao.DAO;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserService {
    private final DAO<User> repository;

    @Autowired
    public UserService(DAO<User> repository) {
        this.repository = repository;
    }

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

    public User getById(Integer id) {
        return repository.get(id).orElseThrow(() -> new ResourceNotFoundException("Could not find this user"));
    }

    public List<User> getByNaturalId(String naturalId, boolean strict) {
        try {
            var users = repository.getByColumn(naturalId, strict);
            if (users == null)
                return Collections.emptyList();
            return users;
        } catch(Exception e) {
            e.printStackTrace();
            throw new ResourceNotFoundException("Could not find users");
        }
    }

    public Integer createUser(User user) {
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


    private Map<String, String> thisNaturalIdExists(User user) {
        Map<String, String> matches = new HashMap<>();

        try {
            var phoneNumberMatch = getByNaturalId(user.getPersonalInfo().getPhoneNumber(), true);
            if (phoneNumberMatch.size() != 0)
                matches.put("phoneNumber", "User with this phone number already exists");
        } catch(Exception ignored) {}

        try {
            var emailMatch = getByNaturalId(user.getPersonalInfo().getEmail(), true);
            if (emailMatch.size() != 0)
                matches.put("email", "User with this phone number already exists");
        } catch(Exception ignored) {}

        return matches;
    }
}
