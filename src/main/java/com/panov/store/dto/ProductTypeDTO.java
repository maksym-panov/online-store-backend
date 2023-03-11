package com.panov.store.dto;

import com.panov.store.model.ProductType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;

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

    public static ProductTypeDTO of(ProductType pt) {
        if (pt == null)
            return null;

        return new ProductTypeDTO(
                pt.getProductTypeId(),
                pt.getName()
        );
    }

    public ProductType toModel() {
        var pt = new ProductType();
        pt.setProductTypeId(productTypeId);
        pt.setName(name);
        pt.setProducts(new HashSet<>());
        return pt;
    }
}
