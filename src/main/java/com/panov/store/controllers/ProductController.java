package com.panov.store.controllers;

import com.panov.store.model.Product;
import com.panov.store.services.ProductService;
import com.panov.store.utils.ListUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/products")
public class ProductController {
    private final ProductService service;

    @Autowired
    public ProductController(ProductService productService) {
        this.service = productService;
    }

    @GetMapping
    public List<Product> productsRange(
            @RequestBody(required = false) String pattern,
            @RequestParam(name = "quantity", required = false) Integer quantity,
            @RequestParam(name = "offset", required = false) Integer offset) {
        if (pattern == null)
            return ListUtils.makeCut(service.getRangeOfProducts(), quantity, offset);
        return ListUtils.makeCut(service.getByNamePattern(pattern), quantity, offset);
    }


    @GetMapping("/{id}")
    public Product specificProduct(@PathVariable("id") Integer id) {
        return service.getById(id);
    }
}
