package com.panov.store.services;

import com.panov.store.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.panov.store.dao.DAO;
import com.panov.store.exceptions.UserNotFoundException;

import java.util.Collections;
import java.util.List;

@Service
public class UserService {
    private final DAO<User> repository;

    @Autowired
    public UserService(DAO<User> repository) {
        this.repository = repository;
    }

    public List<User> getAllUserList() {
        return repository.getAll();
    }

    public User getById(Integer id) {
        return repository.get(id).orElseThrow(UserNotFoundException::new);
    }

    public List<User> getByNaturalId(String naturalId) {
        var users = repository.getByColumn(naturalId);
        if (users == null)
            return Collections.emptyList();
        return users;
    }
}
