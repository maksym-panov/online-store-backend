package com.panov.store.dto;

import com.panov.store.model.ProductType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;

/**
 * Data transfer abstraction class, designed for communication between <br>
 * frontend and backend represented by this application. Wraps {@link ProductType} objects.
 *
 * @author Maksym Panov
 * @version 1.0
 * @see ProductType
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductTypeDTO {
    private Integer productTypeId;

    @NotNull(message = "Category name must be present")
    @Size(min = 2, message = "Category name must be meaningful")
    @Size(max = 30, message = "Category name is too long")
    private String name;

    /**
     * Static factory that converts specified {@link ProductType} object <br>
     * to a new instance of {@link ProductTypeDTO}
     *
     * @param pt a {@link ProductType} object to convert
     * @return a transfer-safe {@link ProductTypeDTO} abstraction
     */
    public static ProductTypeDTO of(ProductType pt) {
        if (pt == null)
            return null;

        return new ProductTypeDTO(
                pt.getProductTypeId(),
                pt.getName()
        );
    }

    /**
     * Converts current {@link ProductTypeDTO} object to an instance of {@link ProductType}.
     *
     * @return a {@link ProductType} instance
     */
    public ProductType toModel() {
        var pt = new ProductType();
        pt.setProductTypeId(productTypeId);
        pt.setName(name);
        pt.setProducts(new ArrayList<>());
        return pt;
    }
}
