package com.panov.store.dao;

import com.panov.store.model.Product;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class ProductRepository implements DAO<Product> {
    private final EntityManagerFactory entityManagerFactory;

    @Autowired
    public ProductRepository(EntityManagerFactory entityManagerFactory) {
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
        Optional<Product> product = Optional.ofNullable((Product) entityManager
                .createQuery("select p from Product p where :col = :val")
                .setParameter("col", naturalId)
                .setParameter("val", value)
                .getSingleResult()
        );
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
        entityManager.persist(product);
        entityManager.close();
        return product.getProductId();
    }

    @Override
    public Integer update(Product product) {
        var entityManager = getManager();
        entityManager.merge(product);
        entityManager.close();
        return product.getProductId();
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
