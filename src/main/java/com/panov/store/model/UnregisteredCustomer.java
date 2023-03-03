package com.panov.store.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Entity
@Table(name = "UnregisteredCustomer")
public class UnregisteredCustomer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer unregisteredCustomerId;
    private String phoneNumber;
    private String firstname;
    private String lastname;

    @Embedded
    private Address address;

    protected UnregisteredCustomer() {}
}
