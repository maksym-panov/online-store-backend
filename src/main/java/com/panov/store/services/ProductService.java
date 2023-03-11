package com.panov.store.services;

import com.panov.store.dao.DAO;
import com.panov.store.exceptions.ResourceNotCreatedException;
import com.panov.store.exceptions.ResourceNotUpdatedException;
import com.panov.store.exceptions.ResourceNotFoundException;
import com.panov.store.model.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.panov.store.exceptions.ResourceNotDeletedException;

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
        }
        return Collections.emptyList();
    }

    public Product getById(Integer id) {
        return repository.get(id)
                .orElseThrow(() -> new ResourceNotFoundException("Could not find this product"));
    }

    public List<Product> getByNamePattern(String namePattern) {
        try {
            var products = repository.getByColumn(namePattern);
            if (products == null)
                return Collections.emptyList();
            return products;
        } catch(Exception e) {
            e.printStackTrace();
        }
        return Collections.emptyList();
    }

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

    public void deleteProduct(Integer id) {
        try {
            repository.delete(id);
        } catch(Exception e) {
            e.printStackTrace();
            throw new ResourceNotDeletedException("Could not delete this product");
        }
    }

    private Map<String, String> thisNaturalIdExists(Product product) {
        Map<String, String> matches = new HashMap<>();

        try {
            var nameMatch = getByNamePattern(product.getName());
            if (nameMatch.size() != 0)
                matches.put("name", "Product with this name already exists");
        } catch(Exception ignored) {}

        return matches;
    }
}
