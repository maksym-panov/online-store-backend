package com.panov.store.controllers;

import com.panov.store.dto.ProductDTO;
import com.panov.store.exceptions.ResourceNotCreatedException;
import com.panov.store.exceptions.ResourceNotUpdatedException;
import com.panov.store.model.Product;
import com.panov.store.services.ProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Web controller that handles requests associated with {@link Product}. <br>
 *
 * @author Maksym Panov
 * @version 2.0
 * @see ProductDTO
 * @see ProductService
 */
@RestController
@RequestMapping("/api/v2/products")
public class ProductController {
    private final ProductService service;

    @Autowired
    public ProductController(ProductService productService) {
        this.service = productService;
    }

    /**
     * Returns a list of all {@link Product} objects. There is <br>
     * also a possibility of using parameters in the request link <br>
     * to customize the output. <br><br>
     * HTTP method: {@code GET} <br>
     * Endpoint: {@code /products{?pattern=&category=&quantity=&offset=}} <br>
     *
     * @param pattern if specified, the method will search for products
     *                with {@code pattern} in the name (case-insensitive).
     * @param typeId if specified, the method will search for products with
     *               {@link Product} which has {@code typeId} as identifier.
     * @param quantity if specified, the method will return only the first
     *                 {@code quantity} products.
     * @param offset if specified, the method will skip first {@code offset}
     *               products.
     * @return a list of {@link Product} objects.
     */
    @GetMapping
    public List<ProductDTO> productsRange(
            @RequestBody(required = false) String pattern,
            @RequestParam(name = "category", required = false) Integer typeId,
            @RequestParam(name = "quantity", required = false) Integer quantity,
            @RequestParam(name = "offset", required = false) Integer offset) {
        List<Product> products;
        if (pattern == null || pattern.isBlank())
            products = service.getRangeOfProducts(offset, quantity);
        else
            products = service.getByNamePattern(pattern, false);

        return products
                .stream()
                .map(ProductDTO::of)
                .filter(p -> typeId == null || p.inCategory(typeId))
                .toList();
    }

    /**
     * Retrieves a {@link Product} with specified ID. <br><br>
     * HTTP method: {@code GET} <br>
     * Endpoint: {@code /products/{productId}} <br>
     *
     * @param id an identifier of a {@link Product}
     * @return retrieved product instance with specified identifier
     */
    @GetMapping("/{id}")
    public ProductDTO specificProduct(@PathVariable("id") Integer id) {
        return ProductDTO.of(service.getById(id));
    }

    /**
     * Creates and saves new {@link Product} instance. <br><br>
     * HTTP method: {@code POST} <br>
     * Endpoint: /products <br>
     *
     * @param productDTO a data transfer object for {@link Product}
     * @param bindingResult a Hibernate Validator object which keeps all
     *                      validation violations.
     * @return an identifier of created {@link Product}
     */
    @PostMapping
    public Integer createProduct(@Valid @RequestBody ProductDTO productDTO,
                                         BindingResult bindingResult) {
        if (bindingResult.hasErrors())
            throw new ResourceNotCreatedException(bindingResult);

        if (productDTO == null)
            throw new ResourceNotCreatedException("Could not create this product");

        return service.createProduct(productDTO.toModel());
    }

    /**
     * Changes information of {@link Product} object with specified ID. <br><br>
     * Http method: {@code PATCH} <br>
     * Endpoint: /products/{productId} <br>
     *
     * @param productDTO a data transfer object for {@link Product}.
     * @param id an identifier of a product which user wants to change.
     * @param bindingResult a Hibernate Validator object which keeps all
     *                      validation violations.
     * @return an identifier of provided {@link Product}.
     */
    @PatchMapping("/{id}")
    public Integer changeProduct(@Valid @RequestBody ProductDTO productDTO,
                                 @PathVariable("id") Integer id,
                                 BindingResult bindingResult) {
        if (bindingResult.hasErrors())
            throw new ResourceNotUpdatedException(bindingResult);

        productDTO.setProductId(id);

        return service.changeProduct(productDTO.toModel());
    }
}
