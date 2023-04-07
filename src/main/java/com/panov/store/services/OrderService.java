package com.panov.store.services;

import com.panov.store.dao.DAO;
import com.panov.store.exceptions.ResourceNotCreatedException;
import com.panov.store.exceptions.ResourceNotFoundException;
import com.panov.store.exceptions.ResourceNotUpdatedException;
import com.panov.store.model.Order;
import com.panov.store.model.UnregisteredCustomer;
import com.panov.store.model.User;
import com.panov.store.utils.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Timestamp;
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

    @PreAuthorize("hasAuthority('ADMINISTRATOR') or hasAuthority('MANAGER')")
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

    @PreAuthorize("hasAuthority('ADMINISTRATOR') or hasAuthority('MANAGER')")
    public Order getById(Integer id) {
        return repository.get(id).orElseThrow(() ->
                new ResourceNotFoundException("Could not find this order")
        );
    }

    @PreAuthorize(
            "hasAuthority('ADMINISTRATOR') or " +
            "hasAuthority('MANAGER') or " +
            "hasAuthority(#userId.toString())"
    )
    public List<Order> getOrdersByUser(Integer userId) {
        User user = userService.getById(userId);
        var list = user.getOrders();
        if (list == null)
            return Collections.emptyList();

        return list;
    }

    @PreAuthorize("hasAuthority('ADMINISTRATOR') or hasAuthority('MANAGER')")
    public List<Order> getOrdersByUnregisteredCustomer(Integer unregCustId) {
        UnregisteredCustomer customer = unregCustService.getById(unregCustId);
        var list = customer.getOrders();
        if (list == null)
            return Collections.emptyList();

        return list;
    }

    @PreAuthorize("" +
            "(#order.user != null and hasAuthority(#order.user.userId.toString())) or " +
            "#order.unregisteredCustomer != null or " +
            "hasAuthority('ADMINISTRATOR') or " +
            "hasAuthority('MANAGER')"
    )
    public Integer createOrder(Order order) {
        if (order.getUser() == null && order.getUnregisteredCustomer() == null ||
            order.getUser() != null && order.getUnregisteredCustomer() != null)
            throw new ResourceNotCreatedException("Could not post this order");

        Integer id = null;

        if (order.getOrderProducts() != null) {
            calculateSumsInOrderProducts(order);
            calculateTotal(order);
        }

        order.setPostTime(new Timestamp(System.currentTimeMillis() - 10000));
        order.setStatus(Status.POSTED);

        try {
            if (order.getUnregisteredCustomer() != null)
                unregCustService.createUnregisteredCustomer(order.getUnregisteredCustomer());
            id = repository.insert(order);
        } catch(Exception e) {
            e.printStackTrace();
        }

        if (id == null)
            throw new ResourceNotCreatedException("Could not post this order");

        return id;
    }

    @PreAuthorize("hasAuthority('ADMINISTRATOR') or hasAuthority('MANAGER')")
    public Integer changeOrder(Order order) {
        Integer id = null;

        if (order.getOrderProducts() != null) {
            if (order.getStatus() != Status.COMPLETED)
                calculateSumsInOrderProducts(order);
            calculateTotal(order);
        }

        try {
            id = repository.update(order);
        } catch(Exception e) {
            e.printStackTrace();
        }

        if (id == null)
            throw new ResourceNotUpdatedException("Could not change this order");

        return id;
    }

    private void calculateSumsInOrderProducts(Order order) {
        for (var op : order.getOrderProducts()) {
            var productPrice = op.getProduct().getPrice();
            var quantity =  BigDecimal.valueOf(op.getQuantity());

            var sum = productPrice.multiply(quantity);

            op.setSum(sum);
        }
    }

    private void calculateTotal(Order order) {
        BigDecimal total = BigDecimal.ZERO;

        for (var op : order.getOrderProducts()) {
            total = total.add(op.getSum());
        }

        order.setTotal(total);
    }
}
