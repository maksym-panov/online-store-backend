package com.panov.store.services;

import com.panov.store.dao.DAO;
import com.panov.store.model.UnregisteredCustomer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.panov.store.exceptions.UnregisteredCustomerNotFoundException;

import java.util.Collections;
import java.util.List;

@Service
public class UnregisteredCustomerService {
    private final DAO<UnregisteredCustomer> repository;

    @Autowired
    public UnregisteredCustomerService(DAO<UnregisteredCustomer> repository) {
        this.repository = repository;
    }

    public List<UnregisteredCustomer> getUnregCustomerList() {
        var list = repository.getAll();
        if (list == null)
            return Collections.emptyList();
        return list;
    }

    public UnregisteredCustomer getById(Integer id) {
        return repository.get(id).orElseThrow(UnregisteredCustomerNotFoundException::new);
    }

    public List<UnregisteredCustomer> getByPhoneNumber(String phoneNumber) {
        var customers = repository.getByColumn(phoneNumber);
        if (customers == null)
            return Collections.emptyList();
        return customers;
    }
}
