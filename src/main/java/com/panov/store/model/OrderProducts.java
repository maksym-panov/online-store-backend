package com.panov.store.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * This class represents order items in the Domain.
 *
 * @author Maksym Panov
 * @version 1.0
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@Entity
@Table(name = "OrderProducts")
public class OrderProducts {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer orderProductsId;

    @NotNull(message = "There must be product in the order line")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "productId")
    private Product product;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "orderId")
    private Order order;

    @NotNull(message = "Quantity of ordered product cannot be null")
    @Min(value = 1, message = "Quantity must be greater than 0")
    private Integer quantity;

    @NotNull(message = "Sum cannot be null")
    @Min(value = 0, message = "Sum cannot be negative")
    private BigDecimal sum;

    @Override
    public int hashCode() {
        return Objects.hash(product, quantity);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null)
            return false;
        if (!(o instanceof OrderProducts other))
            return false;
        return Objects.equals(this.orderProductsId, other.orderProductsId);
    }
}
