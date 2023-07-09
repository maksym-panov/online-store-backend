package com.panov.store.controllers;

import com.panov.store.dto.DeliveryTypeDTO;
import com.panov.store.exceptions.ResourceNotCreatedException;
import com.panov.store.exceptions.ResourceNotUpdatedException;
import com.panov.store.services.DeliveryTypeService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import com.panov.store.model.DeliveryType;

import java.util.List;
import java.util.Objects;

/**
 * Web controller that handles requests associated with {@link DeliveryType}. <br>
 *
 * @author Maksym Panov
 * @version 2.0
 * @see DeliveryTypeDTO
 * @see DeliveryTypeService
 */
@RestController
@RequestMapping("/api/v2/delivery_types")
public class DeliveryTypeController {
    private final DeliveryTypeService service;

    @Autowired
    public DeliveryTypeController(DeliveryTypeService service) {
        this.service = service;
    }

    /**
     * Returns a list of all {@link DeliveryType} objects. There is <br>
     * also a possibility of using parameters in the request link <br>
     * to customize the output. <br><br>
     * HTTP method: {@code GET} <br>
     * Endpoint: {@code /delivery_types{?pattern=&quantity=&offset=}} <br>
     *
     * @param pattern if specified, the method will search for delivery types
     *                with {@code pattern} in the name (case-insensitive).
     * @param quantity if specified, the method will return only the first
     *                 {@code quantity} delivery types.
     * @param offset if specified, the method will skip first {@code offset}
     *               delivery types.
     * @return a list of {@link DeliveryType} objects.
     */
    @GetMapping
    public List<DeliveryTypeDTO> deliveryTypesRange(
            @RequestBody(required = false) String pattern,
            @RequestParam(name = "quantity", required = false) Integer quantity,
            @RequestParam(name = "offset", required = false) Integer offset) {
        List<DeliveryType> types;
        if (pattern == null || pattern.isBlank())
            types = service.getDeliveryTypeList(offset, quantity);
        else types = service.getByNamePattern(pattern, false);

        return types
                .stream()
                .filter(Objects::nonNull)
                .map(DeliveryTypeDTO::of)
                .toList();
    }

    /**
     * Retrieves a {@link DeliveryType} with specified ID. <br><br>
     * HTTP method: {@code GET} <br>
     * Endpoint: {@code /delivery_types/{deliveryTypeId}} <br>
     *
     * @param id an identifier of a {@link DeliveryType}
     * @return retrieved delivery type instance with specified identifier
     */
    @GetMapping("/{id}")
    public DeliveryTypeDTO specificDeliveryType(@PathVariable("id") Integer id) {
        return DeliveryTypeDTO.of(service.getById(id));
    }

    /**
     * Creates and saves new {@link DeliveryType} instance. <br><br>
     * HTTP method: {@code POST} <br>
     * Endpoint: /delivery_types <br>
     *
     * @param deliveryTypeDTO a data transfer object for {@link DeliveryType}
     * @param bindingResult a Hibernate Validator object which keeps all
     *                      validation violations.
     * @return an identifier of created {@link DeliveryType}
     */
    @PostMapping
    public Integer createDeliveryType(@Valid @RequestBody DeliveryTypeDTO deliveryTypeDTO,
                                     BindingResult bindingResult) {
        if (bindingResult.hasErrors())
            throw new ResourceNotCreatedException(bindingResult);

        return service.createDeliveryType(deliveryTypeDTO.toModel());
    }

    /**
     * Changes information of {@link DeliveryType} object with specified ID. <br><br>
     * Http method: {@code PATCH} <br>
     * Endpoint: /delivery_types/{deliveryTypeId} <br>
     *
     * @param deliveryTypeDTO a data transfer object for {@link DeliveryType}.
     * @param id an identifier of a delivery type which user wants to change.
     * @param bindingResult a Hibernate Validator object which keeps all
     *                      validation violations.
     * @return an identifier of provided {@link DeliveryType}.
     */
    @PatchMapping("/{id}")
    public Integer changeDeliveryType(@Valid @RequestBody DeliveryTypeDTO deliveryTypeDTO,
                                     @PathVariable("id") Integer id,
                                     BindingResult bindingResult) {
        if (bindingResult.hasErrors())
            throw new ResourceNotUpdatedException(bindingResult);

        deliveryTypeDTO.setDeliveryTypeId(id);

        return service.changeDeliveryType(deliveryTypeDTO.toModel());
    }

    /**
     * Deletes a {@link DeliveryType} object with a specified identifier. <br><br>
     * HTTP method: {@code DELETE} <br>
     * Endpoint: /delivery_types/{deliveryTypeId} <br>
     *
     * @param id an identifier of a delivery type which user wants to delete.
     * @return if deletion is successful, returns a HTTP response status 200 (OK).
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteDeliveryType(@PathVariable("id") Integer id) {
        service.deleteDeliveryType(id);
        return ResponseEntity.ok(HttpStatus.OK);
    }
}
