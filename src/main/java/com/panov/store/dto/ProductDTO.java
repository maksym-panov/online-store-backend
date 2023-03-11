package com.panov.store.dto;

import com.panov.store.model.Product;
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
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

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

    private Set<ProductTypeDTO> productTypes = new HashSet<>();

    public boolean inCategory(Integer productTypeId) {
        for (var pt : productTypes)
            if (Objects.equals(productTypeId, pt.getProductTypeId()))
                return true;
        return false;
    }

    public static ProductDTO of(Product p) {
        if (p == null)
            return null;

        return new ProductDTO(
                p.getProductId(),
                p.getName(),
                p.getDescription(),
                p.getPrice(),
                p.getStock(),
                p.getProductTypes()
                        .stream()
                        .filter(Objects::nonNull)
                        .map(ProductTypeDTO::of)
                        .collect(Collectors.toSet())
        );
    }

    public Product toModel() {
        var p = new Product();
        p.setProductId(productId);
        p.setName(name);
        p.setDescription(description);
        p.setPrice(price);
        p.setStock(stock);
        p.setProductTypes(
                productTypes == null ? new HashSet<>() :
                        productTypes.stream()
                                .filter(Objects::nonNull)
                                .map(ProductTypeDTO::toModel)
                                .collect(Collectors.toSet())
        );
        return p;
    }
}
