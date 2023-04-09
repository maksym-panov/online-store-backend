package com.panov.store.dto;

import com.panov.store.model.*;
import com.panov.store.utils.Status;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Data transfer abstraction class, designed for communication between <br>
 * frontend and backend represented by this application. Wraps {@link Order} objects.
 *
 * @author Maksym Panov
 * @version 1.0
 * @see Order
 * @see OrderProductsDTO
 * @see DeliveryTypeDTO
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderDTO {
    private Integer orderId;

    private UserDTO user;

    private UnregisteredCustomerDTO unregCust;

    private List<OrderProductsDTO> orderProducts = new ArrayList<>();

    private DeliveryTypeDTO deliveryType;

    private BigDecimal total;

    @NotNull
    @PastOrPresent
    private Timestamp postTime;
    @PastOrPresent
    private Timestamp completeTime;

    @NotNull
    @Convert(converter = Status.StatusConverter.class)
    private Status status;

    /**
     * Static factory that converts specified {@link Order} object <br>
     * to a new instance of {@link OrderDTO}
     *
     * @param o a {@link Order} object to convert
     * @return a transfer-safe {@link OrderDTO} abstraction
     */
    public static OrderDTO of(Order o) {
        if (o == null)
            return null;

        return new OrderDTO(
                o.getOrderId(),
                UserDTO.of(o.getUser()),
                UnregisteredCustomerDTO.of(o.getUnregisteredCustomer()),
                o.getOrderProducts()
                        .stream()
                        .map(OrderProductsDTO::of)
                        .toList(),
                DeliveryTypeDTO.of(o.getDeliveryType()),
                o.getTotal(),
                o.getPostTime(),
                o.getCompleteTime(),
                o.getStatus()
        );
    }

    /**
     * Converts current {@link OrderDTO} object to an instance of {@link Order}.
     *
     * @return a {@link Order} instance
     */
    public Order toModel() {
        var o = new Order();
        o.setOrderId(orderId);
        o.setUser(user == null ? null : user.toModel());
        o.setUnregisteredCustomer(unregCust == null ? null : unregCust.toModel());
        o.setOrderProducts(
                orderProducts == null ? new ArrayList<>() :
                orderProducts.stream()
                        .filter(Objects::nonNull)
                        .map(OrderProductsDTO::toModel)
                        .toList()
        );
        o.setDeliveryType(deliveryType == null ? null : deliveryType.toModel());
        o.setTotal(total);
        o.setPostTime(postTime);
        o.setCompleteTime(completeTime);
        o.setStatus(status);
        return o;
    }
}
