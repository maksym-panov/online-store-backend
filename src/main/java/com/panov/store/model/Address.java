package com.panov.store.model;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Embeddable
public class Address {
    @Size(max = 30, message = "Region name is too long")
    private String region;
    @Size(max = 30, message = "District name is too long")
    private String district;
    @Size(max = 30, message = "City name is too long")
    private String city;
    @Size(max = 30, message = "Street name is too long")
    private String street;
    @Min(value = 1, message = "Building number must be greater than 0")
    @Max(value = 32767, message = "Building number is too big")
    private Integer building;
    @Min(value = 1, message = "Apartment number must be greater than 0")
    @Max(value = 32767, message = "Apartment number is too big")
    private Integer apartment;
    @Min(value = 7000, message = "Invalid postal code")
    @Max(value = 99999, message = "Invalid postal code")
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