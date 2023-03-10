package com.panov.store.controllers;

import com.panov.store.dto.ProductDTO;
import com.panov.store.exceptions.products.ProductNotCreatedException;
import com.panov.store.exceptions.products.ProductNotUpdatedException;
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
            @RequestParam(name = "quantity", required = false) Integer quantity,
            @RequestParam(name = "offset", required = false) Integer offset) {
        List<ProductDTO> range;
        if (pattern == null)
            range = service.getRangeOfProducts()
                    .stream()
                    .map(ProductDTO::of)
                    .toList();
        else
            range = service.getByNamePattern(pattern)
                    .stream()
                    .map(ProductDTO::of)
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
            throw new ProductNotCreatedException(bindingResult);
        Integer id = service.createProduct(productDTO.toModel());
        if (id == null)
            throw new ProductNotCreatedException();
        return id;
    }

    @PatchMapping("/{id}")
    public Integer changeProduct(@RequestBody @Valid ProductDTO productDTO,
                                 @PathVariable("id") Integer id,
                                 BindingResult bindingResult) {
        if (bindingResult.hasErrors())
            throw new ProductNotUpdatedException(bindingResult);
        Product product = null;
        if (productDTO != null) {
            productDTO.setProductId(id);
            product = productDTO.toModel();
        }
        return service.changeProduct(product);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteProduct(@PathVariable("id") Integer id) {
        service.deleteProduct(id);
        return ResponseEntity.ok(HttpStatus.OK);
    }
}
