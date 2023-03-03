package com.panov.store.model;

import com.panov.store.utils.Status;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
@AllArgsConstructor
public class Order {
    private Integer orderId;
    private User user;
    private UnregisteredCustomer unregisteredCustomer;
    private Timestamp postTime;
    private Timestamp completeTime;
    private Status status;
}
