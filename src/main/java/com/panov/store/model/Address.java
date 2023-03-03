package com.panov.store.model;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Embeddable
public class Address {
    private Integer regionId;
    private String district;
    private String city;
    private String street;
    private Integer building;
    private Integer apartment;
    private Integer postalCode;

    protected Address() {}

    @Override
    public String toString() {
        return String.format("Address: %s dst., village %s, %s st., building %d %n Postal code: %d",
                district,
                city,
                street,
                building,
                postalCode
        );
    }
}