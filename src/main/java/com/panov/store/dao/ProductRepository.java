package com.panov.store.dao;

import com.panov.store.model.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.*;

/**
 * The repository of {@link Product} objects. Implements {@link DAO} interface.
 *
 * @author Maksym Panov
 * @version 1.0
 */
@Repository
public class ProductRepository implements DAO<Product> {
    private final EntityManagerFactory entityManagerFactory;

    @Autowired
    public ProductRepository(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    /**
     * Retrieves an {@link Product} object from the database by its identity.
     *
     * @param id an identifier of the {@link Product} which user wants to retrieve
     * @return an optional of the sought {@link Product} object.
     */
    @Override
    public Optional<Product> get(int id) {
        var entityManager = getManager();

        Optional<Product> product;
        try {
            product = Optional.ofNullable(entityManager.find(Product.class, id));
        } finally {
            entityManager.close();
        }
        return product;
    }

    /**
     * Retrieves all the products from the database.
     *
     * @return a list of all {@link Product} that exist in the database
     */
    @Override
    public List<Product> getPackage(Integer offset, Integer quantity) {
        var entityManager = getManager();

        if (offset == null || offset < 0)
            offset = 0;
        if (quantity == null || quantity < 0)
            quantity = 500;

        List<Product> products;
        try {
            products = entityManager
                    .createQuery("select p from Product p", Product.class)
                    .setFirstResult(offset)
                    .setMaxResults(quantity)
                    .getResultList();
        } finally {
            entityManager.close();
        }
        return products;
    }

    public List<Product> getPackageByProductType(Integer offset, Integer quantity, Integer typeId) {
        var entityManager = getManager();

        if (offset == null || offset < 0)
            offset = 0;
        if (quantity == null || quantity < 0)
            quantity = 500;

        List<Product> products;
        try {
            products = entityManager
                    .createQuery("select p from Product p inner join p.productTypes pt where pt.productTypeId = :id", Product.class)
                    .setParameter("id", typeId)
                    .setFirstResult(offset)
                    .setMaxResults(quantity)
                    .getResultList();
        } finally {
            entityManager.close();
        }
        return products;
    }

    /**
     * Retrieves the products that match a specified name pattern regarding offset and quantity.
     *
     * @return a list of products that match the given pattern {@code value}
     */
    @Override
    public List<Product> getByColumn(Object value, Integer offset, Integer quantity, boolean strict) {
        if (value == null || value.toString().isBlank()) {
            return Collections.emptyList();
        }

        var entityManager = getManager();

        List<Product> products;
        try {
            String probablyName = Objects.toString(value);
            if (!strict)
                probablyName = "%" + probablyName + "%";

            if (offset == null || offset < 0)
                offset = 0;
            if (quantity == null || quantity < 0)
                quantity = 500;

            products = entityManager
                    .createQuery(
                            "select p from Product p where lower(p.name) LIKE lower(:pattern)",
                            Product.class
                    )
                    .setParameter("pattern", probablyName)
                    .setFirstResult(offset)
                    .setMaxResults(quantity)
                    .getResultList();
        } finally {
            entityManager.close();
        }
        return products;
    }

    /**
     * Saves new {@link Product} to the database.
     *
     * @param product a {@link Product} to save
     * @return an identity of saved {@link Product} object
     */
    @Override
    public Integer insert(Product product) {
        var entityManager = getManager();
        try {
            entityManager.getTransaction().begin();

            entityManager.persist(product);

            var types = product.getProductTypes();
            List<ProductType> productTypes = new ArrayList<>();
            for (var t : types) {
                var pt = entityManager.find(ProductType.class, t.getProductTypeId());
                pt.getProducts().add(product);
                productTypes.add(pt);
            }

            product.setProductTypes(productTypes);

            entityManager.persist(product);

            entityManager.getTransaction().commit();
        } finally {
            entityManager.close();
        }
        return product.getProductId();
    }

    /**
     * Updates information about {@link Product} object.
     *
     * @param product an object with update information
     * @return an identity of changed {@link Product} object.
     */
    @Override
    public Integer update(Product product) {
        var entityManager = getManager();

        try {
            entityManager.getTransaction().begin();

            var current = entityManager.find(Product.class, product.getProductId());

            if (product.getProductTypes() == null)
                product.setProductTypes(new ArrayList<>());

            for (var pt : current.getProductTypes()) {
                if (!product.getProductTypes().contains(pt)) {
                    pt.getProducts().remove(current);
                }
            }

            for (var pt : product.getProductTypes()) {
                if (!current.getProductTypes().contains(pt)) {
                    try {
                        var productType = entityManager.find(ProductType.class, pt.getProductTypeId());
                        productType.getProducts().add(product);
                        current.getProductTypes().add(productType);
                    } catch (Exception ignored) {}
                }
            }

            current.getProductTypes().retainAll(product.getProductTypes());

            entityManager.merge(product);

            entityManager.getTransaction().commit();
        } finally {
            entityManager.close();
        }
        return product.getProductId();
    }

    @Override
    public void delete(Integer id) {
        throw new UnsupportedOperationException();
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
