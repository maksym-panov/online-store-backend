package com.panov.store.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity(name = "OrderProducts")
@Table(name = "OrderProducts")
@JsonIgnoreProperties({ "order" })
public class OrderProducts {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer orderProductsId;

    @NotNull(message = "There must be product in the order line")
    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "productId")
    private Product product;

    @NotNull(message = "There must be order reference in the order line")
    @ManyToOne
    @JoinColumn(name = "orderId")
    private Order order;

    @NotNull(message = "Quantity of ordered product cannot be null")
    @Min(value = 1, message = "Quantity must be greater than 0")
    private Integer quantity;

    @Override
    public String toString() {
        return String.format("OrderProduct[id = %d, product = %s, quantity = %d, order = %d]",
                orderProductsId,
                product,
                quantity,
                order.getOrderId()
        );
    }
}
