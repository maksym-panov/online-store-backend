package com.panov.store.controllers;

import com.panov.store.model.Order;
import com.panov.store.services.OrderService;
import com.panov.store.utils.ListUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
    public List<Order> orderRange(@RequestParam(name = "user", required = false) Integer userId,
                                  @RequestParam(name = "uc", required = false) Integer unregCustId,
                                  @RequestParam(name = "quantity", required = false) Integer quantity,
                                  @RequestParam(name = "offset", required = false) Integer offset) {
        List<Order> result;
        if (userId == null && unregCustId == null)
            result = service.getAllOrdersList();
        else if (userId == null)
            result = service.getOrdersByUnregisteredCustomer(unregCustId);
        else
            result = service.getOrdersByUser(userId);
        return ListUtils.makeCut(result, quantity, offset);
    }

    @GetMapping("/{id}")
    public Order specificOrder(@PathVariable("id") Integer id) {
        return service.getById(id);
    }
}
