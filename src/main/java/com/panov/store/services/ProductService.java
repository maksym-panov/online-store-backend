package com.panov.store.services;

import com.panov.store.dao.DAO;
import com.panov.store.exceptions.ResourceNotCreatedException;
import com.panov.store.exceptions.ResourceNotUpdatedException;
import com.panov.store.exceptions.ResourceNotFoundException;
import com.panov.store.model.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
public class ProductService {
    private final DAO<Product> repository;

    @Autowired
    public ProductService(DAO<Product> productRepository) {
        this.repository = productRepository;
    }

    public List<Product> getRangeOfProducts() {
        try {
            List<Product> productRange = repository.getAll();
            if (productRange == null)
                return Collections.emptyList();
            return productRange;
        } catch(Exception e) {
            e.printStackTrace();
            throw new ResourceNotFoundException("Could not find products");
        }
    }

    public Product getById(Integer id) {
        return repository.get(id)
                .orElseThrow(() -> new ResourceNotFoundException("Could not find this product"));
    }

    public List<Product> getByNamePattern(String namePattern, boolean strict) {
        try {
            var products = repository.getByColumn(namePattern, strict);
            if (products == null)
                return Collections.emptyList();
            return products;
        } catch(Exception e) {
            e.printStackTrace();
            throw new ResourceNotFoundException("Could not find products");
        }
    }

    @PreAuthorize("hasAuthority('ADMINISTRATOR') or hasAuthority('MANAGER')")
    public Integer createProduct(Product product) {
        Map<String, String> matches = thisNaturalIdExists(product);
        if (matches.size() != 0)
            throw new ResourceNotCreatedException(matches);

        Integer id = null;

        try {
            id = repository.insert(product);
        } catch(Exception e) {
            e.printStackTrace();
        }

        if (id == null)
            throw new ResourceNotCreatedException("Could not create this product");

        return id;
    }

    @PreAuthorize("hasAuthority('ADMINISTRATOR') or hasAuthority('MANAGER')")
    public Integer changeProduct(Product product) {
        Map<String, String> matches = thisNaturalIdExists(product);
        if (matches.size() != 0)
            throw new ResourceNotUpdatedException(matches);

        Integer id = null;

        try {
            id = repository.update(product);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (id == null)
            throw new ResourceNotUpdatedException("Could not update this product");

        return id;
    }

    private Map<String, String> thisNaturalIdExists(Product product) {
        Map<String, String> matches = new HashMap<>();

        try {
            var nameMatch = getByNamePattern(product.getName(), true);
            if (nameMatch.size() != 0 && !product.getProductId().equals(nameMatch.get(0).getProductId()))
                matches.put("name", "Product with this name already exists");
        } catch(Exception ignored) {}

        return matches;
    }
}
