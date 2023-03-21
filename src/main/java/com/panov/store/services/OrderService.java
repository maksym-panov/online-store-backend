package com.panov.store.services;

import com.panov.store.dao.DAO;
import com.panov.store.exceptions.ResourceNotCreatedException;
import com.panov.store.exceptions.ResourceNotFoundException;
import com.panov.store.exceptions.ResourceNotUpdatedException;
import com.panov.store.model.Order;
import com.panov.store.model.UnregisteredCustomer;
import com.panov.store.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
        try {
            var list = repository.getAll();
            if (list == null)
                return Collections.emptyList();
            return list;
        } catch(Exception e) {
            e.printStackTrace();
            throw new ResourceNotFoundException("Could not find products");
        }
    }

    public Order getById(Integer id) {
        return repository.get(id).orElseThrow(() -> new ResourceNotFoundException("Could not find this order"));
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

    public Integer createOrder(Order order) {
        Integer id = null;

        try {
            id = repository.insert(order);
        } catch(Exception e) {
            e.printStackTrace();
        }

        if (id == null)
            throw new ResourceNotCreatedException("Could not post this order");

        return id;
    }

    public Integer changeOrder(Order order) {
        Integer id = null;

        try {
            id = repository.update(order);
        } catch(Exception e) {
            e.printStackTrace();
        }

        if (id == null)
            throw new ResourceNotUpdatedException("Could not change this order");

        return id;
    }
}
