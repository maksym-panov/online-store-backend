package com.panov.store.dto;

import com.panov.store.model.DeliveryType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;

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

    public static DeliveryTypeDTO of(DeliveryType dt) {
        if (dt == null)
            return null;

        return new DeliveryTypeDTO(
                dt.getDeliveryTypeId(),
                dt.getName()
        );
    }

    public DeliveryType toModel() {
        var dt = new DeliveryType();
        dt.setDeliveryTypeId(deliveryTypeId);
        dt.setName(name);
        dt.setOrders(new ArrayList<>());
        return dt;
    }
}
