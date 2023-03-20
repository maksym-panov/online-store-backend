package com.panov.store.controllers;

import com.panov.store.dto.DeliveryTypeDTO;
import com.panov.store.exceptions.ResourceNotCreatedException;
import com.panov.store.exceptions.ResourceNotUpdatedException;
import com.panov.store.services.DeliveryTypeService;
import com.panov.store.utils.ListUtils;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import com.panov.store.model.DeliveryType;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/delivery_types")
public class DeliveryTypeController {
    private final DeliveryTypeService service;

    @Autowired
    public DeliveryTypeController(DeliveryTypeService service) {
        this.service = service;
    }

    @GetMapping
    public List<DeliveryTypeDTO> productTypesRange(
            @RequestBody(required = false) String pattern,
            @RequestParam(name = "quantity", required = false) Integer quantity,
            @RequestParam(name = "offset", required = false) Integer offset) {
        List<DeliveryType> types;
        if (pattern == null) types = service.getDeliveryTypeList();
        else types = service.getByNamePattern(pattern, false);

        var productTypes = types
                .stream()
                .filter(Objects::nonNull)
                .map(DeliveryTypeDTO::of)
                .toList();

        return ListUtils.makeCut(productTypes, quantity, offset);
    }

    @GetMapping("/{id}")
    public DeliveryTypeDTO specificProductType(@PathVariable("id") Integer id) {
        return DeliveryTypeDTO.of(service.getById(id));
    }

    @PostMapping
    public Integer createProductType(@RequestBody @Valid DeliveryTypeDTO deliveryTypeDTO,
                                     BindingResult bindingResult) {
        if (bindingResult.hasErrors())
            throw new ResourceNotCreatedException(bindingResult);

        return service.createDeliveryType(deliveryTypeDTO.toModel());
    }

    @PatchMapping("/{id}")
    public Integer changeProductType(@RequestBody @Valid DeliveryTypeDTO deliveryTypeDTO,
                                     @PathVariable("id") Integer id,
                                     BindingResult bindingResult) {
        if (bindingResult.hasErrors())
            throw new ResourceNotUpdatedException(bindingResult);

        deliveryTypeDTO.setDeliveryTypeId(id);

        return service.changeDeliveryType(deliveryTypeDTO.toModel());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteProductType(@PathVariable("id") Integer id) {
        service.deleteDeliveryType(id);
        return ResponseEntity.ok(HttpStatus.OK);
    }
}
