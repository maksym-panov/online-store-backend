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
        var productType = Optional.ofNullable(entityManager.find(ProductType.class, id));
        entityManager.close();
        return productType;
    }

    @Override
    public List<ProductType> getAll() {
        var entityManager = getManager();
        var productTypes = entityManager.createQuery("select pt from ProductType pt", ProductType.class)
                .getResultList();
        entityManager.close();
        return productTypes;
    }

    @Override
    public List<ProductType> getByColumn(Object value) {
        var entityManager = getManager();
        String probablyName = Objects.toString(value);
        probablyName = "%" + probablyName + "%";
        var productTypes = entityManager
                .createQuery("select pt from ProductType pt where lower(pt.name) like lower(:pattern)", ProductType.class)
                .setParameter("pattern", probablyName)
                .getResultList();
        entityManager.close();
        return productTypes;
    }

    @Override
    public Integer insert(ProductType productType) {
        var entityManager = getManager();
        entityManager.getTransaction().begin();
        entityManager.persist(productType);
        entityManager.getTransaction().commit();
        entityManager.close();
        return productType.getProductTypeId();
    }

    @Override
    public Integer update(ProductType productType) {
        var entityManager = getManager();
        entityManager.getTransaction().begin();
        entityManager.merge(productType);
        entityManager.getTransaction().commit();
        entityManager.close();
        return productType.getProductTypeId();
    }

    @Override
    public void delete(ProductType productType) {
        var entityManager = getManager();
        entityManager.getTransaction().begin();
        entityManager.remove(productType);
        entityManager.getTransaction().commit();
        entityManager.close();
    }

    private EntityManager getManager() {
        return entityManagerFactory.createEntityManager();
    }
}
