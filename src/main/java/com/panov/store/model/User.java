package com.panov.store.model;

import com.panov.store.utils.Access;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class User {
    private Integer userId;
    private String phoneNumber;
    private String email;
    private String firstname;
    private String lastname;
    private String password;
    private Access access;
    private Integer regionId;
    private String district;
    private String city;
    private String street;
    private Integer building;
    private Integer apartment;
    private Integer postalCode;
}
