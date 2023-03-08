package com.panov.store.controllers;

import com.panov.store.model.Order;
import com.panov.store.services.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {
    private final OrderService service;

    @Autowired
    public OrderController(OrderService service) {
        this.service = service;
    }

    @GetMapping
    public List<Order> orderRange() {
        return service.getAllOrdersList();
    }

    @GetMapping("/{id}")
    public Order specificOrder(@PathVariable("id") Integer id) {
        return service.getById(id);
    }
}
