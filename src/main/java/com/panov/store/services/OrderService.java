package com.panov.store.services;

import com.panov.store.dao.DAO;
import com.panov.store.model.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.panov.store.exceptions.OrderNotFoundException;

import java.util.Collections;
import java.util.List;

@Service
public class OrderService {
    private final DAO<Order> repository;

    @Autowired
    public OrderService(DAO<Order> repository) {
        this.repository = repository;
    }

    public List<Order> getAllOrdersList() {
        var list = repository.getAll();
        if (list == null)
            return Collections.emptyList();
        return list;
    }

    public Order getById(Integer id) {
        return repository.get(id).orElseThrow(OrderNotFoundException::new);
    }
}
