package com.panov.store.dao;

import com.panov.store.model.Product;
import com.panov.store.model.ProductType;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.*;

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
    public List<Product> getByColumn(Object value, boolean strict) {
        var entityManager = getManager();
        if (value == null)
            return null;

        String probablyName = Objects.toString(value);
        if (!strict)
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

        var types = product.getProductTypes();
        List<ProductType> productTypes = new ArrayList<>();
        for (var t : types) {
            if (t == null || t.getProductTypeId() == null)
                continue;
            if (entityManager.find(ProductType.class, t.getProductTypeId()) == null)
                continue;
            productTypes.add(entityManager.find(ProductType.class, t.getProductTypeId()));
        }
        product.setProductTypes(productTypes);
        entityManager.persist(product);

        entityManager.getTransaction().commit();
        entityManager.close();
        return product.getProductId();
    }

    @Override
    public Integer update(Product product) {
        var entityManager = getManager();
        entityManager.getTransaction().begin();

        var types = product.getProductTypes();
        if (types == null)
            types = new ArrayList<>();
        List<ProductType> productTypes = new ArrayList<>();
        for (var t : types) {
            try {
                productTypes.add(entityManager.find(ProductType.class, t.getProductTypeId()));
            } catch(Exception ignored) {}
        }
        product.setProductTypes(productTypes);
        entityManager.merge(product);

        entityManager.getTransaction().commit();
        entityManager.close();
        return product.getProductId();
    }

    @Override
    public void delete(Integer id) {
        var entityManager = getManager();
        entityManager.getTransaction().begin();
        entityManager.remove(entityManager.find(Product.class, id));
        entityManager.getTransaction().commit();
        entityManager.close();
    }

    private EntityManager getManager() {
        return entityManagerFactory.createEntityManager();
    }
}
