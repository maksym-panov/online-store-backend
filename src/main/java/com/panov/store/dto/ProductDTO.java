package com.panov.store.dto;

import com.panov.store.model.Product;
import com.panov.store.model.ProductType;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductDTO {
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
    private Set<ProductType> productTypes = new HashSet<>();

    public static ProductDTO of(Product p) {
        return new ProductDTO(
                p.getProductId(),
                p.getName(),
                p.getDescription(),
                p.getPrice(),
                p.getStock(),
                p.getProductTypes()
        );
    }

    public Product toModel() {
        var p = new Product();
        p.setProductId(productId);
        p.setName(name);
        p.setDescription(description);
        p.setPrice(price);
        p.setStock(stock);
        p.setProductTypes(productTypes);
        return p;
    }
}
