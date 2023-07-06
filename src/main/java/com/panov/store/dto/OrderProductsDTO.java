package com.panov.store.dto;

import com.panov.store.model.OrderProducts;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Data transfer abstraction class, designed for communication between <br>
 * frontend and backend represented by this application. Wraps {@link OrderProducts} objects.
 *
 * @author Maksym Panov
 * @version 1.0
 * @see OrderProducts
 * @see ProductDTO
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderProductsDTO {
    @NotNull(message = "There must be product in this order line")
    private ProductDTO product;

    @NotNull(message = "Quantity of ordered product cannot be null")
    @Min(value = 1, message = "Quantity must be greater than 0")
    private Integer quantity;

    /**
     * Static factory that converts specified {@link OrderProducts} object <br>
     * to a new instance of {@link OrderProductsDTO}
     *
     * @param op a {@link OrderProducts} object to convert
     * @return a transfer-safe {@link OrderProductsDTO} abstraction
     */
    public static OrderProductsDTO of(OrderProducts op) {
        if (op == null)
            return null;

        return new OrderProductsDTO(
            ProductDTO.of(op.getProduct()),
            op.getQuantity()
        );
    }

    /**
     * Converts current {@link OrderProductsDTO} object to an instance of {@link OrderProducts}.
     *
     * @return a {@link OrderProducts} instance
     */
    public OrderProducts toModel() {
        var op = new OrderProducts();
        op.setProduct(product.toModel());
        op.setQuantity(quantity);
        return op;
    }
}
