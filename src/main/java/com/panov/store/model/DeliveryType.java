package com.panov.store.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.*;

/**
 * This class represents delivery types in the Domain.
 *
 * @author Maksym Panov
 * @version 1.0
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@Entity
@Table(name = "DeliveryType")
public class DeliveryType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer deliveryTypeId;

    @Column(unique = true)
    private String name;

    @ToString.Exclude
    @OneToMany(mappedBy = "deliveryType", fetch = FetchType.EAGER)
    private List<Order> orders = new ArrayList<>();

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        if (!(o instanceof DeliveryType other)) return false;
        return Objects.equals(this.name, other.name) ||
                (deliveryTypeId != null && deliveryTypeId.equals(other.deliveryTypeId));
    }


}
