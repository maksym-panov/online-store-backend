package com.panov.store.services;

import com.panov.store.dao.DAO;
import com.panov.store.dao.ProductRepository;
import com.panov.store.exceptions.ResourceNotCreatedException;
import com.panov.store.exceptions.ResourceNotUpdatedException;
import com.panov.store.exceptions.ResourceNotFoundException;
import com.panov.store.model.Product;
import com.panov.store.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service-layer class that processes {@link Product} entities.
 *
 * @author Maksym Panov
 * @version 1.0
 * @see DAO
 * @see ProductRepository
 */
@Service
public class ProductService {
    private final DAO<Product> repository;

    @Autowired
    public ProductService(DAO<Product> productRepository) {
        this.repository = productRepository;
    }

    /**
     * Uses {@link DAO} implementation to retrieve list of all existing {@link Product} entities. <br><br>
     * Re-throws a {@link ResourceNotFoundException} if {@link DAO} object throws an exception.
     *
     * @return a {@link List} of {@link Product} objects
     */
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

    /**
     * Uses {@link DAO} implementation to retrieve a {@link Product} entity by specified identity. <br><br>
     * Re-throws a {@link ResourceNotFoundException} if {@link DAO} object throws an exception or there is
     * no such {@link Product} object.
     *
     * @param id an identity of the sought {@link Product}
     * @return a {@link Product} object with specified identity
     */
    public Product getById(Integer id) {
        return repository.get(id)
                .orElseThrow(() -> new ResourceNotFoundException("Could not find this product"));
    }

    /**
     * Searches for {@link Product} objects using {@link DAO} implementation by specified
     * name pattern. <br><br>
     * Re-throws a {@link ResourceNotFoundException} if {@link DAO} object throws an exception.
     *
     * @param namePattern a name pattern you want to search by
     * @param strict if {@code true} returns exact matches and if {@code false}
     *               returns a list of {@link Product} objects whose names contain
     *               {@code namePattern} as their part (case-insensitive)
     * @return a list of {@link Product} objects that match specified pattern
     */
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

    /**
     * Uses {@link DAO} implementation to save new {@link Product} in the data storage. <br><br>
     * Only {@link User} with {@code Access.ADMINISTRATOR} or {@code Access.MANAGER} authority
     * can invoke this method. <br><br>
     * Re-throws a {@link ResourceNotCreatedException} if {@link DAO} object throws an exception.
     *
     * @param product an object to save
     * @return an identity of saved {@link Product}
     */
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

    /**
     * Uses {@link DAO} implementation to change information of {@link Product}. <br><br>
     * Only {@link User} with {@code Access.ADMINISTRATOR} or {@code Access.MANAGER} authority
     * can invoke this method. <br><br>
     * Re-throws a {@link ResourceNotUpdatedException} if {@link DAO} object throws an exception.
     *
     * @param product an object that contains new data and identity of {@link Product}
     *                     that should be updated
     * @return an identity of updated {@link Product}
     */
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

    /**
     * Checks if the name of provided object is already used in existing {@link Product} object.
     *
     * @param product an object to check
     * @return a {@link Map} that contains decision about uniqueness of the name of specified {@link Product}
     */
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
