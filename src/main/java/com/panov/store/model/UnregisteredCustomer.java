package com.panov.store.model;

import jakarta.persistence.*;
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
    private String phoneNumber;
    private String firstname;
    private String lastname;

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
