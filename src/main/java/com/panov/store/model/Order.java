package com.panov.store.model;

import com.panov.store.utils.Status;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "Order")
@Table(name = "\"Order\"")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer orderId;

    @ManyToOne
    @JoinColumn(name = "userId")
    private User user;

    @ManyToOne
    @JoinColumn(name = "unregisteredCustomerId")
    private UnregisteredCustomer unregisteredCustomer;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<OrderProducts> orderProducts = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "deliveryTypeId")
    private DeliveryType deliveryType;

    private Timestamp postTime;
    private Timestamp completeTime;

    @Convert(converter = Status.StatusConverter.class)
    private Status status;

    @Override
    public String toString() {
        return String.format("Order[id = %d, user = %s, unregisteredCustomer = %s, " +
                        "postTime = %s, completeTime = %s, status = %s, " +
                        "orderProducts = %s, deliveryType = %s]",
                orderId,
                user,
                unregisteredCustomer,
                postTime,
                completeTime,
                status,
                orderProducts,
                deliveryType
        );
    }
}
