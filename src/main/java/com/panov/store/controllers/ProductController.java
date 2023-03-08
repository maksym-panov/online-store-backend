package com.panov.store.controllers;

import com.panov.store.model.Product;
import com.panov.store.services.ProductService;
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
    public List<Product> productRange() {
        return service.getRangeOfProducts();
    }

    @GetMapping("/{id}")
    public Product specificProduct(@PathVariable("id") Integer id) {
        return service.getById(id);
    }
}
