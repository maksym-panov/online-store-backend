package com.panov.store.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.*;

/**
 * This class represents products in the Domain.
 *
 * @author Maksym Panov
 * @version 1.0
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@Entity
@Table(name = "Product")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer productId;

    @NotNull(message = "Product name must be present")
    @Size(min = 2, message = "Product name must be meaningful")
    @Size(max = 128, message = "Product name is too long")
    @Column(unique = true)
    private String name;

    private String description;

    @NotNull(message = "Product price must be present")
    @Min(value = 0L, message = "Price cannot be a negative number")
    @Max(value = 99999999L, message = "Price is too big")
    private BigDecimal price;

    @NotNull(message = "Stock of product must be present")
    @Min(value = 0, message = "Stock must be greater than 0")
    private Integer stock;

    @ManyToMany(fetch = FetchType.EAGER)
    private List<ProductType> productTypes = new ArrayList<>();

    @ToString.Exclude
    @OneToMany(mappedBy = "product", fetch = FetchType.EAGER)
    private List<OrderProducts> orderProducts = new ArrayList<>();

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null)
            return false;
        if (!(o instanceof Product other))
            return false;
        return Objects.equals(this.name, other.name);
    }
}
