package com.panov.store.dao;

import com.panov.store.model.*;
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

        Optional<Order> order;

        try {
            order = Optional.ofNullable(entityManager.find(Order.class, id));
            if (order.isPresent() && order.get().getUser() != null && order.get().getUser().getAddress() == null)
                order.get().getUser().setAddress(new Address());
        } finally {
            entityManager.close();
        }

        return order;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Order> getAll() {
        var entityManager = getManager();

        List<Order> orders;

        try {
            orders = (List<Order>) entityManager
                    .createQuery("select o from Order o")
                    .getResultList();
            for (var o : orders) {
                if (o.getUser() != null && o.getUser().getAddress() == null)
                    o.getUser().setAddress(new Address());
            }
        } finally {
            entityManager.close();
        }

        return orders;
    }

    @Override
    public List<Order> getByColumn(Object value, boolean strict) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Integer insert(Order order) {
        var entityManager = getManager();

        try {
            entityManager.getTransaction().begin();

            // Attach this order to its User-owner
            var u = order.getUser();
            if (u != null) {
                u = entityManager.find(User.class, u.getUserId());
                u.getOrders().add(order);
            }

            // Attach this order to its UnregisteredCustomer-owner
            var uc = order.getUnregisteredCustomer();
            if (uc != null) {
                uc = entityManager.find(UnregisteredCustomer.class, uc.getUnregisteredCustomerId());
                uc.getOrders().add(order);
            }

            // Attach this order to its DeliveryType
            var dt = order.getDeliveryType();
            dt = entityManager.find(DeliveryType.class, dt.getDeliveryTypeId());
            order.setDeliveryType(dt);

            // Attach all the OrderProducts to this order
            for (var op : order.getOrderProducts())
                op.setOrder(order);

            // Save an actual order to the persistence
            entityManager.persist(order);

            entityManager.getTransaction().commit();
        } finally {
            entityManager.close();
        }

        return order.getOrderId();
    }

    @Override
    public Integer update(Order order) {
        var entityManager = getManager();

        try {
            entityManager.getTransaction().begin();

            var current = entityManager.find(Order.class, order.getOrderId());

            // Delete order products that were deleted
            for (var op : current.getOrderProducts()) {
                if (!order.getOrderProducts().contains(op)) {
                    op.getProduct().getOrderProducts().remove(op);
                    op.setOrder(null);
                }
            }

            for (var op : order.getOrderProducts()) {
                if (current.getOrderProducts().contains(op)) {
                    entityManager.merge(op);
                    continue;
                }
                entityManager.persist(op);
                op.setOrder(current);
                current.getOrderProducts().add(op);
            }

            current.getOrderProducts().retainAll(order.getOrderProducts());

            // Update delivery type
            var currentDeliveryType = current.getDeliveryType();
            if (!currentDeliveryType.equals(order.getDeliveryType())) {
                currentDeliveryType.getOrders().remove(current);

                var toSetDeliveryType = entityManager.find(
                        DeliveryType.class,
                        order.getDeliveryType().getDeliveryTypeId()
                );
                toSetDeliveryType.getOrders().add(current);
                current.setDeliveryType(toSetDeliveryType);
            }

            // Update a status of the order
            current.setStatus(order.getStatus());

            // Update a completion time of the order
            current.setCompleteTime(order.getCompleteTime());

            entityManager.getTransaction().commit();
        } finally {
            entityManager.close();
        }

        return order.getOrderId();
    }

    @Override
    public void delete(Integer id) {
        throw new UnsupportedOperationException();
    }

    private EntityManager getManager() {
        return entityManagerFactory.createEntityManager();
    }

}
