package com.panov.store.services;

import com.panov.store.dao.DAO;
import com.panov.store.model.Order;
import com.panov.store.model.UnregisteredCustomer;
import com.panov.store.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.panov.store.exceptions.orders.OrderNotFoundException;

import java.util.Collections;
import java.util.List;

@Service
public class OrderService {
    private final DAO<Order> repository;
    private final UserService userService;
    private final UnregisteredCustomerService unregCustService;

    @Autowired
    public OrderService(DAO<Order> repository, UserService userService,
                        UnregisteredCustomerService unregCustService) {
        this.repository = repository;
        this.userService = userService;
        this.unregCustService = unregCustService;
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

    public List<Order> getOrdersByUser(Integer userId) {
        User user = userService.getById(userId);
        var list = user.getOrders();
        if (list == null)
            return Collections.emptyList();
        return list;
    }

    public List<Order> getOrdersByUnregisteredCustomer(Integer unregCustId) {
        UnregisteredCustomer customer = unregCustService.getById(unregCustId);
        var list = customer.getOrders();
        if (list == null)
            return Collections.emptyList();
        return list;
    }
}
