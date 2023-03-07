package com.panov.store.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.NaturalId;

import java.util.Objects;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "UnregisteredCustomer")
@Table(name = "UnregisteredCustomer")
public class UnregisteredCustomer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer unregisteredCustomerId;

    @NaturalId
    private String phoneNumber;
    private String firstname;
    private String lastname;

    @Embedded
    private Address address;

    @OneToMany(mappedBy = "unregisteredCustomer")
    private Set<Order> orders;

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
