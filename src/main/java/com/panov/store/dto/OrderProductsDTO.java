package com.panov.store.dto;

import com.panov.store.model.OrderProducts;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderProductsDTO {
    private Integer orderProductsId;

    @NotNull(message = "There must be product in this order line")
    private ProductDTO product;

    @NotNull(message = "Quantity of ordered product cannot be null")
    @Min(value = 1, message = "Quantity must be greater than 0")
    private Integer quantity;

    public static OrderProductsDTO of(OrderProducts op) {
        if (op == null)
            return null;

        return new OrderProductsDTO(
            op.getOrderProductsId(),
            ProductDTO.of(op.getProduct()),
            op.getQuantity()
        );
    }

    public OrderProducts toModel() {
        var op = new OrderProducts();
        op.setOrderProductsId(orderProductsId);
        op.setProduct(product.toModel());
        op.setQuantity(quantity);
        return op;
    }
}
