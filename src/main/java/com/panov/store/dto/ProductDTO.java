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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Data transfer abstraction class, designed for communication between <br>
 * frontend and backend represented by this application. Wraps {@link Product} objects.
 *
 * @author Maksym Panov
 * @version 1.0
 * @see Product
 * @see ProductTypeDTO
 */
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

    private String image;

    @NotNull(message = "Product price must be present")
    @Min(value = 0L, message = "Price cannot be a negative number")
    @Max(value = 99999999L, message = "Price is too big")
    private BigDecimal price;

    @NotNull(message = "Stock of product must be present")
    @Min(value = 0, message = "Stock must be greater than 0")
    private Integer stock;

    private Set<ProductTypeDTO> productTypes = new HashSet<>();

    public boolean inCategory(Integer productTypeId) {
        System.out.println(productTypeId);
        for (var pt : productTypes) {
            System.out.println(pt.getName());
            if (Objects.equals(productTypeId, pt.getProductTypeId()))
                return true;
        }
        return false;
    }

    /**
     * Static factory that converts specified {@link Product} object <br>
     * to a new instance of {@link ProductDTO}
     *
     * @param p a {@link Product} object to convert
     * @return a transfer-safe {@link ProductDTO} abstraction
     */
    public static ProductDTO of(Product p) {
        if (p == null)
            return null;

        return new ProductDTO(
                p.getProductId(),
                p.getName(),
                p.getDescription(),
                p.getImage(),
                p.getPrice(),
                p.getStock(),
                p.getProductTypes()
                        .stream()
                        .filter(Objects::nonNull)
                        .map(ProductTypeDTO::of)
                        .collect(Collectors.toSet())
        );
    }

    /**
     * Converts current {@link ProductDTO} object to an instance of {@link Product}.
     *
     * @return a {@link Product} instance
     */
    public Product toModel() {
        var p = new Product();
        p.setProductId(productId);
        p.setName(name);
        p.setDescription(description);
        p.setImage(image);
        p.setPrice(price);
        p.setStock(stock);
        p.setProductTypes(
                productTypes == null ? new ArrayList<>() :
                        productTypes.stream()
                                .filter(Objects::nonNull)
                                .map(ProductTypeDTO::toModel)
                                .toList()
        );
        return p;
    }
}
