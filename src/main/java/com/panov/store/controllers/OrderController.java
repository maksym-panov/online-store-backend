package com.panov.store.controllers;

import com.panov.store.dto.OrderDTO;
import com.panov.store.dto.OrderProductsDTO;
import com.panov.store.exceptions.ResourceNotCreatedException;
import com.panov.store.exceptions.ResourceNotUpdatedException;
import com.panov.store.model.DeliveryType;
import com.panov.store.model.Order;
import com.panov.store.model.OrderProducts;
import com.panov.store.services.OrderService;
import com.panov.store.common.Utils;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

/**
 * Web controller that handles requests associated with {@link Order}. <br>
 *
 * @author Maksym Panov
 * @version 2.0
 * @see OrderProducts
 * @see OrderDTO
 * @see OrderProductsDTO
 * @see OrderService
 */
@RestController
@RequestMapping("/api/v2/orders")
public class OrderController {
    private final OrderService service;

    @Autowired
    public OrderController(OrderService service) {
        this.service = service;
    }

    /**
     * Returns a list of all {@link Order} objects. There is <br>
     * also a possibility of using parameters in the request link <br>
     * to customize the output. <br><br>
     * HTTP method: {@code GET} <br>
     * Endpoint: {@code /orders{?user=&uc=&quantity=&offset=}} <br>
     *
     * @param userId if specified, the method will return only orders of a user
     *               with this identifier.
     * @param unregCustId if specified, the method will return only orders of an
     *                    unregistered customer with this identifier.
     * @param quantity if specified, the method will return only the first
     *                 {@code quantity} orders.
     * @param offset if specified, the method will skip first {@code offset}
     *               orders.
     * @return a list of {@link DeliveryType} objects.
     */
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

        return Utils.makeCut(result, quantity, offset);
    }

    /**
     * Retrieves an {@link Order} with specified ID. <br><br>
     * HTTP method: {@code GET} <br>
     * Endpoint: {@code /orders/{orderId}} <br>
     *
     * @param id an identifier of the {@link Order}
     * @return retrieved order instance with specified identifier
     */
    @GetMapping("/{id}")
    public OrderDTO specificOrder(@PathVariable("id") Integer id) {
        return OrderDTO.of(service.getById(id));
    }

    /**
     * Creates and saves new {@link Order} instance. <br><br>
     * HTTP method: {@code POST} <br>
     * Endpoint: /orders <br>
     *
     * @param orderDTO a data transfer object for {@link Order}
     * @param bindingResult a Hibernate Validator object which keeps all
     *                      validation violations.
     * @return an identifier of created {@link Order}
     */
    @PostMapping
    public Integer postOrder(@Valid @RequestBody OrderDTO orderDTO,
                             BindingResult bindingResult) {
        if (bindingResult.hasErrors())
            throw new ResourceNotCreatedException(bindingResult);

        return service.createOrder(orderDTO.toModel());
    }

    /**
     * Changes information of {@link Order} object with specified ID. <br><br>
     * Http method: {@code PATCH} <br>
     * Endpoint: /orders/{orderId} <br>
     *
     * @param orderDTO a data transfer object for {@link Order}.
     * @param id an identifier of an order which user wants to change.
     * @param bindingResult a Hibernate Validator object which keeps all
     *                      validation violations.
     * @return an identifier of provided {@link Order}.
     */
    @PatchMapping("/{id}")
    public Integer changeOrder(@Valid @RequestBody OrderDTO orderDTO,
                               @PathVariable("id") Integer id,
                               BindingResult bindingResult) {
        if (bindingResult.hasErrors())
            throw new ResourceNotUpdatedException(bindingResult);

        orderDTO.setOrderId(id);

        return service.changeOrder(orderDTO.toModel());
    }
}
