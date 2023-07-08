package com.panov.store.dto;

import com.panov.store.model.DeliveryType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;

/**
 * Data transfer abstraction class, designed for communication between <br>
 * frontend and backend represented by this application. Wraps {@link DeliveryType} objects.
 *
 * @author Maksym Panov
 * @version 1.0
 * @see DeliveryType
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryTypeDTO {
    private Integer deliveryTypeId;

    @NotNull(message = "Delivery type name cannot be null")
    @Size(min = 2, message = "Delivery type name must be meaningful")
    @Size(max = 50, message = "Delivery type name is too long")
    private String name;

    /**
     * Static factory that converts specified {@link DeliveryType} object <br>
     * to a new instance of {@link DeliveryTypeDTO}
     *
     * @param dt a {@link DeliveryType} object to convert
     * @return a transfer-safe {@link DeliveryTypeDTO} abstraction
     */
    public static DeliveryTypeDTO of(DeliveryType dt) {
        if (dt == null)
            return null;

        return new DeliveryTypeDTO(
                dt.getDeliveryTypeId(),
                dt.getName()
        );
    }

    /**
     * Converts current {@link DeliveryTypeDTO} object to an instance of {@link DeliveryType}.
     *
     * @return a {@link DeliveryType} instance
     */
    public DeliveryType toModel() {
        var dt = new DeliveryType();
        dt.setDeliveryTypeId(deliveryTypeId);
        dt.setName(name);
        dt.setOrders(new ArrayList<>());
        return dt;
    }
}
