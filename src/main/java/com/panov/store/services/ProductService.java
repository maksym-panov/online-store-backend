package com.panov.store.services;

import com.panov.store.dao.DAO;
import com.panov.store.exceptions.products.ProductNotCreatedException;
import com.panov.store.exceptions.products.ProductNotUpdatedException;
import com.panov.store.exceptions.products.ProductNotFoundException;
import com.panov.store.model.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.panov.store.exceptions.products.ProductNotDeletedException;

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

    public List<Product> getByNamePattern(String namePattern) {
        var products = repository.getByColumn(namePattern);
        if (products == null)
            return Collections.emptyList();
        return products;
    }

    public Integer createProduct(Product product) {
        Integer id = repository.insert(product);
        if (id == null)
            throw new ProductNotCreatedException();
        return id;
    }

    public Integer changeProduct(Product product) {
        if (product == null || product.getProductId() == null)
            throw new ProductNotUpdatedException();
        return repository.update(product);
    }

    public void deleteProduct(Integer id) {
        try {
            repository.delete(id);
        } catch(Exception e) {
            e.printStackTrace();
            throw new ProductNotDeletedException();
        }
    }
}
