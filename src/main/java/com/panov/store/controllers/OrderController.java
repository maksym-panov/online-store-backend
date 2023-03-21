package com.panov.store.controllers;

import com.panov.store.dto.OrderDTO;
import com.panov.store.exceptions.ResourceNotCreatedException;
import com.panov.store.exceptions.ResourceNotUpdatedException;
import com.panov.store.model.Order;
import com.panov.store.services.OrderService;
import com.panov.store.utils.ListUtils;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
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

    @PostMapping
    public Integer postOrder(@RequestBody @Valid OrderDTO orderDTO,
                             BindingResult bindingResult) {
        if (bindingResult.hasErrors())
            throw new ResourceNotCreatedException(bindingResult);

        return service.createOrder(orderDTO.toModel());
    }

    @PatchMapping("/{id}")
    public Integer changeOrder(@RequestBody @Valid OrderDTO orderDTO,
                               @PathVariable("id") Integer id,
                               BindingResult bindingResult) {
        if (bindingResult.hasErrors())
            throw new ResourceNotUpdatedException(bindingResult);

        orderDTO.setOrderId(id);

        return service.changeOrder(orderDTO.toModel());
    }
}
