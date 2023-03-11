package com.panov.store.controllers;

import com.panov.store.dto.ProductTypeDTO;
import com.panov.store.exceptions.ResourceNotCreatedException;
import com.panov.store.exceptions.ResourceNotUpdatedException;
import com.panov.store.services.ProductTypeService;
import com.panov.store.utils.ListUtils;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import com.panov.store.model.ProductType;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/product_types")
public class ProductTypeController {
    private final ProductTypeService service;

    @Autowired
    public ProductTypeController(ProductTypeService service) {
        this.service = service;
    }

    @GetMapping
    public List<ProductTypeDTO> productTypesRange(
            @RequestBody(required = false) String pattern,
            @RequestParam(name = "quantity", required = false) Integer quantity,
            @RequestParam(name = "offset", required = false) Integer offset) {
        List<ProductType> types;
        if (pattern == null) types = service.getProductTypeList();
        else types = service.getByNamePattern(pattern);

        var productTypes = types
                .stream()
                .filter(Objects::nonNull)
                .map(ProductTypeDTO::of)
                .toList();

        return ListUtils.makeCut(productTypes, quantity, offset);
    }

    @GetMapping("/{id}")
    public ProductTypeDTO specificProductType(@PathVariable("id") Integer id) {
        return ProductTypeDTO.of(service.getById(id));
    }

    @PostMapping
    public Integer createProductType(@RequestBody @Valid ProductTypeDTO productTypeDTO,
                                     BindingResult bindingResult) {
        if (bindingResult.hasErrors())
            throw new ResourceNotCreatedException(bindingResult);

        return service.createProductType(productTypeDTO.toModel());
    }

    @PatchMapping("/{id}")
    public Integer changeProductType(@RequestBody @Valid ProductTypeDTO productTypeDTO,
                                     @PathVariable("id") Integer id,
                                     BindingResult bindingResult) {
        if (bindingResult.hasErrors())
            throw new ResourceNotUpdatedException(bindingResult);

        productTypeDTO.setProductTypeId(id);

        return service.changeProductType(productTypeDTO.toModel());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteProductType(@PathVariable("id") Integer id) {
        service.deleteProductType(id);
        return ResponseEntity.ok(HttpStatus.OK);
    }
}
