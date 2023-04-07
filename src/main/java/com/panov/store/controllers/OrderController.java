package com.panov.store.controllers;

import com.panov.store.dto.OrderDTO;
import com.panov.store.exceptions.ResourceNotCreatedException;
import com.panov.store.exceptions.ResourceNotUpdatedException;
import com.panov.store.model.Order;
import com.panov.store.services.OrderService;
import com.panov.store.utils.ListUtils;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/orders")
public class OrderController {
    private final OrderService service;

    @Autowired
    public OrderController(OrderService service) {
        this.service = service;
    }

    @GetMapping
    public List<OrderDTO> orderRange(@RequestParam(name = "user", required = false) Integer userId,
                                  @RequestParam(name = "uc", required = false) Integer unregCustId,
                                  @RequestParam(name = "quantity", required = false) Integer quantity,
                                  @RequestParam(name = "offset", required = false) Integer offset) {
        List<Order> orders;

        if (userId == null && unregCustId == null)
            orders = service.getAllOrdersList();
        else if (userId == null)
            orders = service.getOrdersByUnregisteredCustomer(unregCustId);
        else
            orders = service.getOrdersByUser(userId);

        List<OrderDTO> result = orders.stream()
                .filter(Objects::nonNull)
                .map(OrderDTO::of)
                .toList();

        return ListUtils.makeCut(result, quantity, offset);
    }

    @GetMapping("/{id}")
    public OrderDTO specificOrder(@PathVariable("id") Integer id) {
        return OrderDTO.of(service.getById(id));
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
