package com.panov.store.dto;

import com.panov.store.model.Address;
import com.panov.store.model.UnregisteredCustomer;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;

/**
 * Data transfer abstraction class, designed for communication between <br>
 * frontend and backend represented by this application. Wraps {@link UnregisteredCustomer} objects.
 *
 * @author Maksym Panov
 * @version 1.0
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UnregisteredCustomerDTO {
    private Integer unregisteredCustomerId;

    @NotNull(message = "Phone number must be present")
    @Pattern(regexp = "0\\d{9}", message = "Phone number must match the format '0XXXXXXXXX'")
    private String phoneNumber;

    @NotNull(message = "This field is required")
    @Size(min = 1, message = "Firstname cannot be empty")
    @Size(max = 30, message = "Firstname is too long")
    private String firstname;

    @Size(max = 30, message = "Lastname is too long")
    private String lastname;

    @NotNull(message = "This field is required")
    @Size(max = 30, message = "Region name is too long")
    private String region;

    @NotNull(message = "This field is required")
    @Size(max = 30, message = "District name is too long")
    private String district;

    @NotNull(message = "This field is required")
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

    @Min(value = 1001, message = "Invalid postal code")
    @Max(value = 99999, message = "Invalid postal code")
    private Integer postalCode;

    /**
     * Static factory that converts specified {@link UnregisteredCustomer} object <br>
     * to a new instance of {@link UnregisteredCustomerDTO}
     *
     * @param uc a {@link UnregisteredCustomer} object to convert
     * @return a transfer-safe {@link UnregisteredCustomerDTO} abstraction
     */
    public static UnregisteredCustomerDTO of(UnregisteredCustomer uc) {
        if (uc == null)
            return null;

        var address = uc.getAddress();

        if (address == null)
            throw new IllegalStateException("Unregistered customer must have address");

        return new UnregisteredCustomerDTO(
                uc.getUnregisteredCustomerId(),
                uc.getPhoneNumber(),
                uc.getFirstname(),
                uc.getLastname(),
                address.getRegion(),
                address.getDistrict(),
                address.getCity(),
                address.getStreet(),
                address.getBuilding(),
                address.getApartment(),
                address.getPostalCode()
        );
    }

    /**
     * Converts current {@link UnregisteredCustomerDTO} object to an instance of {@link UnregisteredCustomer}.
     *
     * @return an {@link UnregisteredCustomer} instance
     */
    public UnregisteredCustomer toModel() {
        var uc = new UnregisteredCustomer();
        uc.setUnregisteredCustomerId(unregisteredCustomerId);
        uc.setPhoneNumber(phoneNumber);
        uc.setFirstname(firstname);
        uc.setLastname(lastname);

        var address = new Address();
        address.setRegion(region);
        address.setDistrict(district);
        address.setCity(city);
        address.setStreet(street);
        address.setBuilding(building);
        address.setApartment(apartment);
        address.setPostalCode(postalCode);

        uc.setAddress(address);
        uc.setOrders(new ArrayList<>());
        return uc;
    }
}
