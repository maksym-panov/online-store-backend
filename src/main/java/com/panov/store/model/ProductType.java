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
@Entity(name = "ProductType")
@Table(name = "ProductType")
public class ProductType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer productTypeId;
    @NaturalId
    private String name;

    @ManyToMany(mappedBy = "productTypes")
    private Set<Product> products;

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        if (!(o instanceof ProductType other)) return false;
        return Objects.equals(this.name, other.name);
    }

    @Override
    public String toString() {
        return String.format("ProductType[id = %d, name = %s]",
                productTypeId,
                name
        );
    }
}
