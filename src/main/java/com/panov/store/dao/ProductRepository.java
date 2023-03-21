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

        Optional<Product> product;
        try {
            product = Optional.ofNullable(entityManager.find(Product.class, id));
        } finally {
            entityManager.close();
        }
        return product;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Product> getAll() {
        var entityManager = getManager();

        List<Product> products;
        try {
            products = (List<Product>) entityManager
                    .createQuery("select p from Product p")
                    .getResultList();
        } finally {
            entityManager.close();
        }
        return products;
    }

    @Override
    public List<Product> getByColumn(Object value, boolean strict) {
        var entityManager = getManager();

        List<Product> products;
        try {
            if (value == null)
                return null;

            String probablyName = Objects.toString(value);
            if (!strict)
                probablyName = "%" + probablyName + "%";

            products = entityManager.createQuery("select p from Product p where lower(p.name) LIKE lower(:pattern)", Product.class)
                    .setParameter("pattern", probablyName)
                    .getResultList();
        } finally {
            entityManager.close();
        }
        return products;
    }

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
                    var productType = entityManager.find(ProductType.class, pt.getProductTypeId());
                    productType.getProducts().add(product);
                    current.getProductTypes().add(productType);
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

    private EntityManager getManager() {
        return entityManagerFactory.createEntityManager();
    }
}
