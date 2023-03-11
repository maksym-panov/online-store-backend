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

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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

    @NotNull
    @PastOrPresent
    private Timestamp postTime;
    @PastOrPresent
    private Timestamp completeTime;

    @NotNull
    @Convert(converter = Status.StatusConverter.class)
    private Status status;

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
                o.getPostTime(),
                o.getCompleteTime(),
                o.getStatus()
        );
    }

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
        o.setPostTime(postTime);
        o.setCompleteTime(completeTime);
        o.setStatus(status);
        return o;
    }
}
