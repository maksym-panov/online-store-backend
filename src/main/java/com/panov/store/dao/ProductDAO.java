package com.panov.store.dao;

import com.panov.store.exception.BadRequestException;
import com.panov.store.model.Product;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class ProductDAO implements DAO<Product> {
    private final EntityManagerFactory entityManagerFactory;

    @Autowired
    public ProductDAO(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    @Override
    public Optional<Product> get(int id) {
        var entityManager = getManager();
        var product = Optional.ofNullable(entityManager.find(Product.class, id));
        entityManager.close();
        return product;
    }

    @Override
    public Optional<Product> getByColumn(String naturalId, String value) {
        var entityManager = getManager();
        Optional<Product> product;
        try {
            product = Optional.ofNullable((Product) entityManager
                    .createQuery("select p from Product p where :col = :val")
                    .setParameter("col", naturalId)
                    .setParameter("val", value)
                    .getSingleResult()
            );
        } catch (Exception exception) {
            throw new BadRequestException("Incorrect query", exception);
        }
        entityManager.close();
        return product;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Product> getAll() {
        var entityManager = getManager();
        List<Product> products = (List<Product>) entityManager
                .createQuery("select p from Product p")
                .getResultList();
        entityManager.close();
        return products;
    }

    @Override
    public Integer insert(Product product) {
        var entityManager = getManager();
        try {
            entityManager.persist(product);
            entityManager.close();
            return product.getProductId();
        } catch(Exception exception) {
            throw new BadRequestException("Impossible to add new product", exception);
        }
    }

    @Override
    public Integer update(Product product) {
        var entityManager = getManager();
        try {
            entityManager.merge(product);
            entityManager.close();
            return product.getProductId();
        } catch(Exception exception) {
            throw new BadRequestException("Impossible to update product", exception);
        }
    }

    @Override
    public void delete(Product product) {
        var entityManager = getManager();
        entityManager.remove(product);
        entityManager.close();
    }

    private EntityManager getManager() {
        return entityManagerFactory.createEntityManager();
    }
}
