package com.panov.store.controllers;

import com.panov.store.dto.ProductDTO;
import com.panov.store.exceptions.ResourceNotCreatedException;
import com.panov.store.exceptions.ResourceNotUpdatedException;
import com.panov.store.model.Product;
import com.panov.store.services.ProductService;
import com.panov.store.utils.ListUtils;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
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
    public List<ProductDTO> productsRange(
            @RequestBody(required = false) String pattern,
            @RequestParam(name = "category", required = false) Integer typeId,
            @RequestParam(name = "quantity", required = false) Integer quantity,
            @RequestParam(name = "offset", required = false) Integer offset) {
        List<Product> products;
        if (pattern == null) products = service.getRangeOfProducts();
        else products = service.getByNamePattern(pattern, false);

        var range = products
                .stream()
                .map(ProductDTO::of)
                .filter(p -> typeId == null || p.inCategory(typeId))
                .toList();

        return ListUtils.makeCut(range, quantity, offset);
    }

    @GetMapping("/{id}")
    public ProductDTO specificProduct(@PathVariable("id") Integer id) {
        return ProductDTO.of(service.getById(id));
    }

    @PostMapping
    public Integer createProduct(@RequestBody @Valid ProductDTO productDTO,
                                         BindingResult bindingResult) {
        if (bindingResult.hasErrors())
            throw new ResourceNotCreatedException(bindingResult);

        return service.createProduct(productDTO.toModel());
    }

    @PatchMapping("/{id}")
    public Integer changeProduct(@RequestBody @Valid ProductDTO productDTO,
                                 @PathVariable("id") Integer id,
                                 BindingResult bindingResult) {
        if (bindingResult.hasErrors())
            throw new ResourceNotUpdatedException(bindingResult);

        productDTO.setProductId(id);

        return service.changeProduct(productDTO.toModel());
    }
}
