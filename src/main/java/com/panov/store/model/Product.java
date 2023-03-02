package com.panov.store.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
public class Product {
    private Integer productId;
    private String name;
    private String description;
    // Product Types
    private BigDecimal price;
    private Integer stock;
}
