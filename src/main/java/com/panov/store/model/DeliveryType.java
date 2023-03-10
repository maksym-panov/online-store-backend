package com.panov.store.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.NaturalId;

import java.util.List;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@Entity(name = "DeliveryType")
@Table(name = "DeliveryType")
@JsonIgnoreProperties({ "orders" })
public class DeliveryType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer deliveryTypeId;
    @NotNull(message = "Delivery type name cannot be null")
    @Size(min = 2, message = "Delivery type name must be meaningful")
    @Size(max = 50, message = "Delivery type name is too long")
    @NaturalId
    private String name;

    @OneToMany(mappedBy = "deliveryType")
    private List<Order> orders;

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        if (!(o instanceof DeliveryType other)) return false;
        return Objects.equals(this.name, other.name);
    }

    @Override
    public String toString() {
        return String.format("DeliveryType[id = %d, name = %s]",
                deliveryTypeId,
                name
        );
    }
}
