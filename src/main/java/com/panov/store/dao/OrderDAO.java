package com.panov.store.dao;

import com.panov.store.exception.BadRequestException;
import com.panov.store.model.Order;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class OrderDAO implements DAO<Order> {
    private final EntityManagerFactory entityManagerFactory;

    @Autowired
    public OrderDAO(EntityManagerFactory entityManagerFactory) {
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
        Optional<Order> order;
        try {
            order = Optional.ofNullable((Order) entityManager
                    .createQuery("select o from Order o where :col = :val")
                    .setParameter("col", naturalId)
                    .setParameter("val", value)
                    .getSingleResult()
            );
        } catch (Exception exception) {
            throw new BadRequestException("Incorrect query", exception);
        }
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
        try {
            entityManager.persist(order);
            entityManager.close();
            return order.getOrderId();
        } catch(Exception exception) {
            throw new BadRequestException("Impossible to add new order", exception);
        }
    }

    @Override
    public Integer update(Order order) {
        var entityManager = getManager();
        try {
            entityManager.merge(order);
            entityManager.close();
            return order.getOrderId();
        } catch(Exception exception) {
            throw new BadRequestException("Impossible to update order", exception);
        }
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
