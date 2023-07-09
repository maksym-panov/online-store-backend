package com.panov.store.dao;

import com.panov.store.model.ProductType;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * The repository of {@link ProductType} objects. Implements {@link DAO} interface.
 *
 * @author Maksym Panov
 * @version 1.0
 */
@Repository
public class ProductTypeRepository implements DAO<ProductType> {
    private final EntityManagerFactory entityManagerFactory;

    @Autowired
    public ProductTypeRepository(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    /**
     * Retrieves an {@link ProductType} object from the database by its identity.
     *
     * @param id an identifier of the {@link ProductType} which user wants to retrieve
     * @return an optional of the sought {@link ProductType} object.
     */
    @Override
    public Optional<ProductType> get(int id) {
        var entityManager = getManager();

        Optional<ProductType> productType;

        try {
            productType = Optional.ofNullable(entityManager.find(ProductType.class, id));
        } finally {
            entityManager.close();
        }

        return productType;
    }

    @Override
    public List<ProductType> getPackage(Integer offset, Integer quantity) {
        var entityManager = getManager();

        List<ProductType> productTypes;

        if (offset == null || offset < 0)
            offset = 0;
        if (quantity == null || quantity < 0)
            quantity = 500;

        try {
            productTypes = entityManager
                    .createQuery("select pt from ProductType pt", ProductType.class)
                    .setFirstResult(offset)
                    .setMaxResults(quantity)
                    .getResultList();
        } finally {
            entityManager.close();
        }

        return productTypes;
    }

    /**
     * Retrieves all the product types from the database.
     *
     * @return a list of all {@link ProductType} that exist in the database
     */
    @Override
    public List<ProductType> getByColumn(Object value, boolean strict) {
        if (value == null || value.toString().isBlank()) {
            return Collections.emptyList();
        }

        var entityManager = getManager();

        List<ProductType> productTypes;

        try {
            String probablyName = Objects.toString(value);
            if (!strict)
                probablyName = "%" + probablyName + "%";

            productTypes = entityManager
                    .createQuery("select pt from ProductType pt where lower(pt.name) like lower(:pattern)", ProductType.class)
                    .setParameter("pattern", probablyName)
                    .getResultList();
        } finally {
            entityManager.close();
        }
        return productTypes;
    }

    /**
     * Saves new {@link ProductType} to the database.
     *
     * @param productType a {@link ProductType} to save
     * @return an identity of saved {@link ProductType} object
     */
    @Override
    public Integer insert(ProductType productType) {
        var entityManager = getManager();

        try {
            entityManager.getTransaction().begin();
            entityManager.persist(productType);
            entityManager.getTransaction().commit();
        } finally {
            entityManager.close();
        }

        return productType.getProductTypeId();
    }

    /**
     * Updates information about {@link ProductType} object.
     *
     * @param productType an object with update information
     * @return an identity of changed {@link ProductType} object.
     */
    @Override
    public Integer update(ProductType productType) {
        var entityManager = getManager();

        try {
            entityManager.getTransaction().begin();
            entityManager.merge(productType);
            entityManager.getTransaction().commit();
        } finally {
            entityManager.close();
        }

        return productType.getProductTypeId();
    }

    /**
     * Deletes existing {@link ProductType} from the database by its identity.
     *
     * @param id an identity of the {@link ProductType} to be deleted
     */
    @Override
    public void delete(Integer id) {
        var entityManager = getManager();

        try {
            entityManager.getTransaction().begin();

            // Delete reference from every product object
            var pt = entityManager.find(ProductType.class, id);
            for (var p : pt.getProducts()) {
                p.getProductTypes().remove(pt);
            }

            // Delete actual product type
            entityManager.remove(pt);

            entityManager.getTransaction().commit();
        } finally {
            entityManager.close();
        }
    }

    /**
     * Gets new instance of {@link EntityManager} from {@link EntityManagerFactory} instance.
     *
     * @return an {@link EntityManager} instance
     */
    private EntityManager getManager() {
        return entityManagerFactory.createEntityManager();
    }
}
