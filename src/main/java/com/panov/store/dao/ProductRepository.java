package com.panov.store.dao;

import com.panov.store.model.Product;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Objects;
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
    public List<Product> getByColumn(Object value) {
        var entityManager = getManager();
        if (value == null)
            return null;
        String probablyName = Objects.toString(value);
        probablyName = "%" + probablyName + "%";
        var products = entityManager.createQuery("select p from Product p where lower(p.name) LIKE lower(:pattern)", Product.class)
                .setParameter("pattern", probablyName)
                .getResultList();
        entityManager.close();
        return products;
    }

    @Override
    public Integer insert(Product product) {
        var entityManager = getManager();
        entityManager.getTransaction().begin();
        entityManager.persist(product);
        entityManager.getTransaction().commit();
        entityManager.close();
        return product.getProductId();
    }

    @Override
    public Integer update(Product product) {
        var entityManager = getManager();
        entityManager.getTransaction().begin();
        entityManager.merge(product);
        entityManager.getTransaction().commit();
        entityManager.close();
        return product.getProductId();
    }

    @Override
    public void delete(Product product) {
        var entityManager = getManager();
        entityManager.getTransaction().begin();
        entityManager.remove(product);
        entityManager.getTransaction().commit();
        entityManager.close();
    }

    private EntityManager getManager() {
        return entityManagerFactory.createEntityManager();
    }
}
