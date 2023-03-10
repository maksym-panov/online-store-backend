package com.panov.store.services;

import com.panov.store.dao.DAO;
import com.panov.store.model.ProductType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.panov.store.exceptions.ProductTypeNotFoundException;

import java.util.Collections;
import java.util.List;

@Service
public class ProductTypeService {
    private final DAO<ProductType> repository;

    @Autowired
    public ProductTypeService(DAO<ProductType> repository) {
        this.repository = repository;
    }

    public List<ProductType> getProductTypeList() {
        var list = repository.getAll();
        if (list == null)
            return Collections.emptyList();
        return list;
    }

    public ProductType getById(Integer id) {
        return repository.get(id).orElseThrow(ProductTypeNotFoundException::new);
    }

    public List<ProductType> getByNamePattern(String namePattern) {
        var list = repository.getByColumn(namePattern);
        if (list == null)
            return Collections.emptyList();
        return list;
    }
}
