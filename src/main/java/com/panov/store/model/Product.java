package com.panov.store.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@Entity(name = "Product")
@Table(name = "Product")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer productId;
    private String name;
    private String description;
    private BigDecimal price;
    private Integer stock;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(name = "TypeUnit",
        joinColumns = { @JoinColumn(name = "productId") },
        inverseJoinColumns = { @JoinColumn(name = "productTypeId") })
    private Set<ProductType> productTypes = new HashSet<>();

    @Override
    public String toString() {
        return String.format("Product[id = %d, name = %s, description = %s, price = %s, stock = %d, productTypes = %s]",
                productId,
                name,
                description,
                price.toString(),
                stock,
                productTypes
        );
    }
}
