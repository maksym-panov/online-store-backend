package com.panov.store.dao;

import com.panov.store.model.Order;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class OrderRepository implements DAO<Order> {
    private final EntityManagerFactory entityManagerFactory;

    @Autowired
    public OrderRepository(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    @Override
    public Optional<Order> get(int id) {
        var entityManager = getManager();
        var order = Optional.ofNullable(entityManager.find(Order.class, id));
        entityManager.close();
        return order;
    }

    @Override
    public Optional<Order> getByColumn(String naturalId, String value) {
        var entityManager = getManager();
        Optional<Order> order = Optional.ofNullable((Order) entityManager
                .createQuery("select o from Order o where :col = :val")
                .setParameter("col", naturalId)
                .setParameter("val", value)
                .getSingleResult()
        );
        entityManager.close();
        return order;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Order> getAll() {
        var entityManager = getManager();
        List<Order> orders = (List<Order>) entityManager
                .createQuery("select o from Order o")
                .getResultList();
        entityManager.close();
        return orders;
    }

    @Override
    public Integer insert(Order order) {
        var entityManager = getManager();
        entityManager.persist(order);
        entityManager.close();
        return order.getOrderId();
    }

    @Override
    public Integer update(Order order) {
        var entityManager = getManager();
        entityManager.merge(order);
        entityManager.close();
        return order.getOrderId();
    }

    @Override
    public void delete(Order order) {
        var entityManager = getManager();
        entityManager.remove(order);
        entityManager.close();
    }

    private EntityManager getManager() {
        return entityManagerFactory.createEntityManager();
    }
}
