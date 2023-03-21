package com.panov.store.dao;

import com.panov.store.model.ProductType;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Repository
public class ProductTypeRepository implements DAO<ProductType> {
    private final EntityManagerFactory entityManagerFactory;

    @Autowired
    public ProductTypeRepository(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

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
    public List<ProductType> getAll() {
        var entityManager = getManager();

        List<ProductType> productTypes;

        try {
            productTypes = entityManager.createQuery("select pt from ProductType pt", ProductType.class)
                    .getResultList();
        } finally {
            entityManager.close();
        }

        return productTypes;
    }

    @Override
    public List<ProductType> getByColumn(Object value, boolean strict) {
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

    private EntityManager getManager() {
        return entityManagerFactory.createEntityManager();
    }
}
