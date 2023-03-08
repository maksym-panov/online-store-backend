package com.panov.store.services;

import com.panov.store.dao.DAO;
import com.panov.store.exceptions.ProductNotFoundException;
import com.panov.store.model.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;


@Service
public class ProductService {
    private final DAO<Product> repository;

    @Autowired
    public ProductService(DAO<Product> productRepository) {
        this.repository = productRepository;
    }

    public List<Product> getRangeOfProducts() {
        List<Product> productRange = repository.getAll();
        if (productRange == null)
            return Collections.emptyList();
        return productRange;
    }

    public Product getById(Integer id) {
        return repository.get(id)
                .orElseThrow(ProductNotFoundException::new);
    }
}
