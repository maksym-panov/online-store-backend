package com.panov.store.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UnregisteredCustomer {
    private Integer unregisteredCustomerId;
    private String phoneNumber;
    private String firstname;
    private String lastname;
    private Integer regionId;
    private String district;
    private String city;
    private String street;
    private Integer building;
    private Integer apartment;
}
