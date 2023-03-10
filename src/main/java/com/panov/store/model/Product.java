package com.panov.store.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
    @NotNull(message = "Product name must be present")
    @Size(min = 2, message = "Product name must be meaningful")
    @Size(max = 128, message = "Product name is too long")
    private String name;
    private String description;
    @NotNull(message = "Product price must be present")
    @Min(value = 0L, message = "Price cannot be a negative number")
    @Max(value = 99999999L, message = "Price is too big")
    private BigDecimal price;
    @NotNull(message = "Stock of product must be present")
    @Min(value = 0, message = "Stock must be greater than 0")
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
