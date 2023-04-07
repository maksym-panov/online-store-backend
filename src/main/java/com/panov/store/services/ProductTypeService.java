package com.panov.store.services;

import com.panov.store.dao.DAO;
import com.panov.store.exceptions.ResourceNotCreatedException;
import com.panov.store.exceptions.ResourceNotDeletedException;
import com.panov.store.exceptions.ResourceNotFoundException;
import com.panov.store.exceptions.ResourceNotUpdatedException;
import com.panov.store.model.ProductType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ProductTypeService {
    private final DAO<ProductType> repository;

    @Autowired
    public ProductTypeService(DAO<ProductType> repository) {
        this.repository = repository;
    }

    public List<ProductType> getProductTypeList() {
        try {
            var list = repository.getAll();
            if (list == null)
                return Collections.emptyList();
            return list;
        } catch(Exception e) {
            e.printStackTrace();
            throw new ResourceNotFoundException("Could not find product types");
        }
    }

    public ProductType getById(Integer id) {
        return repository.get(id).orElseThrow(() -> new ResourceNotFoundException("Could not find this product type"));
    }

    public List<ProductType> getByNamePattern(String namePattern, boolean strict) {
        try {
            var list = repository.getByColumn(namePattern, strict);
            if (list == null)
                return Collections.emptyList();
            return list;
        } catch(Exception e) {
            e.printStackTrace();
            throw new ResourceNotFoundException("Could not find product types");
        }
    }

    @PreAuthorize("hasAuthority('ADMINISTRATOR') or hasAuthority('MANAGER')")
    public Integer createProductType(ProductType type) {
        Map<String, String> matches = thisNaturalIdExists(type);
        if (matches.size() != 0)
            throw new ResourceNotCreatedException(matches);

        Integer id = null;

        try {
            id = repository.insert(type);
        } catch(Exception e) {
            e.printStackTrace();
        }

        if (id == null)
            throw new ResourceNotCreatedException("Could not create this product type");

        return id;
    }

    @PreAuthorize("hasAuthority('ADMINISTRATOR') or hasAuthority('MANAGER')")
    public Integer changeProductType(ProductType type) {
        Map<String, String> matches = thisNaturalIdExists(type);
        if (matches.size() != 0)
            throw new ResourceNotUpdatedException(matches);

        Integer id = null;

        try {
            id = repository.update(type);
        } catch(Exception e) {
            e.printStackTrace();
        }

        if (id == null)
            throw new ResourceNotUpdatedException("Could not update this product type");

        return id;
    }

    @PreAuthorize("hasAuthority('ADMINISTRATOR')")
    public void deleteProductType(Integer id) {
        try {
            repository.delete(id);
        } catch(Exception e) {
            e.printStackTrace();
            throw new ResourceNotDeletedException("Could not delete this product type");
        }
    }

    private Map<String, String> thisNaturalIdExists(ProductType productType) {
        Map<String, String> matches = new HashMap<>();

        try {
            var nameMatch = getByNamePattern(productType.getName(), true);
            if (nameMatch.size() != 0)
                matches.put("name", "Product type with this name already exists");
        } catch(Exception ignored) {}

        return matches;
    }
}
