package com.panov.store.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.*;

@Getter
@Setter
@NoArgsConstructor
@Entity(name = "UnregisteredCustomer")
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

    @OneToMany(mappedBy = "unregisteredCustomer", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<Order> orders = new ArrayList<>();

    @Override
    public int hashCode() {
        return Objects.hash(phoneNumber);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        if (!(o instanceof UnregisteredCustomer other)) return false;
        return Objects.equals(this.phoneNumber, other.phoneNumber);
    }

    @Override
    public String toString() {
        return String.format("UnregisteredCustomer[Id = %d, phoneNumber = %s, " +
                        "firstname = %s, lastname = %s, address = %s]",
                unregisteredCustomerId,
                phoneNumber,
                firstname,
                lastname,
                address
        );
    }
}
