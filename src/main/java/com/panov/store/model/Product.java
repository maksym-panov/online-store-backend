package com.panov.store.model;

import jakarta.persistence.*;
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

    @Column(unique = true)
    private String name;

    @Column(length = 10000)
    private String description;

    private String image;

    private BigDecimal price;

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
