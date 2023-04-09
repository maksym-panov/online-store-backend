package com.panov.store.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.*;

/**
 * This class represents unregistered customers in the Domain.
 *
 * @author Maksym Panov
 * @version 1.0
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@Entity
@Table(name = "UnregisteredCustomer")
public class UnregisteredCustomer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer unregisteredCustomerId;

    @NotNull(message = "Phone number must be present")
    @Pattern(regexp = "0\\d{9}", message = "Phone number must match the format '0XXXXXXXXX'")
    private String phoneNumber;

    @NotNull(message = "Firstname must be present")
    @Size(min = 1, message = "Firstname cannot be null")
    @Size(max = 30, message = "Firstname is too long")
    private String firstname;

    @Size(max = 30, message = "Lastname is too long")
    private String lastname;

    @NotNull(message = "Address must be present")
    @Embedded
    private Address address;

    @ToString.Exclude
    @OneToMany(mappedBy = "unregisteredCustomer", fetch = FetchType.EAGER)
    private List<Order> orders = new ArrayList<>();

    @Override
    public int hashCode() {
        return Objects.hash(phoneNumber) * 1000
                + Objects.hash(firstname) * 100
                + Objects.hash(lastname) * 10
                + Objects.hash(address);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        if (!(o instanceof UnregisteredCustomer other)) return false;
        return Objects.equals(phoneNumber, other.phoneNumber) &&
                Objects.equals(firstname, other.firstname) &&
                Objects.equals(lastname, other.lastname) &&
                Objects.equals(address, other.address);
    }
}
