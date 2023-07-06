package com.panov.store.controllers;

import com.panov.store.common.Utils;
import com.panov.store.dto.ProductDTO;
import com.panov.store.dto.ProductTypeDTO;
import com.panov.store.exceptions.ResourceNotCreatedException;
import com.panov.store.exceptions.ResourceNotUpdatedException;
import com.panov.store.services.ProductService;
import com.panov.store.services.ProductTypeService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import com.panov.store.model.ProductType;

import java.util.List;
import java.util.Objects;

/**
 * Web controller that handles requests associated with {@link ProductType}. <br>
 *
 * @author Maksym Panov
 * @version 2.0
 * @see ProductDTO
 * @see ProductService
 */
@RestController
@RequestMapping("/api/v2/product_types")
public class ProductTypeController {
    private final ProductTypeService service;

    @Autowired
    public ProductTypeController(ProductTypeService service) {
        this.service = service;
    }

    /**
     * Returns a list of all {@link ProductType} objects. There is <br>
     * also a possibility of using parameters in the request link <br>
     * to customize the output. <br><br>
     * HTTP method: {@code GET} <br>
     * Endpoint: {@code /product_types{?pattern=&quantity=&offset=}} <br>
     *
     * @param pattern if specified, the method will search for product types
     *                with {@code pattern} in the name (case-insensitive).
     * @param quantity if specified, the method will return only the first
     *                 {@code quantity} product types.
     * @param offset if specified, the method will skip first {@code offset}
     *               product types.
     * @return a list of {@link ProductType} objects.
     */
    @GetMapping
    public List<ProductTypeDTO> productTypesRange(
            @RequestBody(required = false) String pattern,
            @RequestParam(name = "quantity", required = false) Integer quantity,
            @RequestParam(name = "offset", required = false) Integer offset) {
        List<ProductType> types;
        if (pattern == null) types = service.getProductTypeList();
        else types = service.getByNamePattern(pattern, false);

        var productTypes = types
                .stream()
                .filter(Objects::nonNull)
                .map(ProductTypeDTO::of)
                .toList();

        return Utils.makeCut(productTypes, quantity, offset);
    }

    /**
     * Retrieves a {@link ProductType} with specified ID. <br><br>
     * HTTP method: {@code GET} <br>
     * Endpoint: {@code /product_types/{productTypeId}} <br>
     *
     * @param id an identifier of a {@link ProductType}
     * @return retrieved product type instance with specified identifier
     */
    @GetMapping("/{id}")
    public ProductTypeDTO specificProductType(@PathVariable("id") Integer id) {
        return ProductTypeDTO.of(service.getById(id));
    }

    /**
     * Creates and saves new {@link ProductType} instance. <br><br>
     * HTTP method: {@code POST} <br>
     * Endpoint: /product_types <br>
     *
     * @param productTypeDTO a data transfer object for {@link ProductType}
     * @param bindingResult a Hibernate Validator object which keeps all
     *                      validation violations.
     * @return an identifier of created {@link ProductType}
     */
    @PostMapping
    public Integer createProductType(@RequestBody @Valid ProductTypeDTO productTypeDTO,
                                     BindingResult bindingResult) {
        if (bindingResult.hasErrors())
            throw new ResourceNotCreatedException(bindingResult);

        return service.createProductType(productTypeDTO.toModel());
    }

    /**
     * Changes information of {@link ProductType} object with specified ID. <br><br>
     * Http method: {@code PATCH} <br>
     * Endpoint: /product_types/{productTypeId} <br>
     *
     * @param productTypeDTO a data transfer object for {@link ProductType}.
     * @param id an identifier of a product type which user wants to change.
     * @param bindingResult a Hibernate Validator object which keeps all
     *                      validation violations.
     * @return an identifier of provided {@link ProductType}.
     */
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
