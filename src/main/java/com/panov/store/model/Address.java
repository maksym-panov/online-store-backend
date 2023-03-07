package com.panov.store.model;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
public class Address {
    private String region;
    private String district;
    private String city;
    private String street;
    private Integer building;
    private Integer apartment;
    private Integer postalCode;

    @Override
    public String toString() {
        return String.format("Address[%s dst., village %s, %s st., building %d Postal code: %d]",
                district,
                city,
                street,
                building,
                postalCode
        );
    }
}