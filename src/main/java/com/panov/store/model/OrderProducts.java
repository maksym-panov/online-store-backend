package com.panov.store.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
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

    @ManyToOne
    @JoinColumn(name = "productId")
    private Product product;

    @ManyToOne
    @JoinColumn(name = "orderId")
    private Order order;

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
