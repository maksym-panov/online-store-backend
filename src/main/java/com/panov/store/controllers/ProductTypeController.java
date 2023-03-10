package com.panov.store.controllers;

import com.panov.store.services.ProductTypeService;
import com.panov.store.utils.ListUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.panov.store.model.ProductType;

import java.util.List;

@RestController
@RequestMapping("/product_types")
public class ProductTypeController {
    private final ProductTypeService service;

    @Autowired
    public ProductTypeController(ProductTypeService service) {
        this.service = service;
    }

    @GetMapping
    public List<ProductType> productTypesRange(
            @RequestBody(required = false) String pattern,
            @RequestParam(name = "quantity", required = false) Integer quantity,
            @RequestParam(name = "offset", required = false) Integer offset) {
        if (pattern == null)
            return ListUtils.makeCut(service.getProductTypeList(), quantity, offset);
        return ListUtils.makeCut(service.getByNamePattern(pattern), quantity, offset);
    }

    @GetMapping("/{id}")
    public ProductType specificProductType(@PathVariable("id") Integer id) {
        return service.getById(id);
    }
}
