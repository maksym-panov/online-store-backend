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
    private final UnregisteredCustomerRepository ucr;

    @Autowired
    public OrderRepository(EntityManagerFactory entityManagerFactory,
                           UnregisteredCustomerRepository ucr) {
        this.entityManagerFactory = entityManagerFactory;
        this.ucr = ucr;
    }

    @Override
    public Optional<Order> get(int id) {
        var entityManager = getManager();
        var order = Optional.ofNullable(entityManager.find(Order.class, id));
        if (order.isPresent() && order.get().getUser() != null && order.get().getUser().getAddress() == null)
            order.get().getUser().setAddress(new Address());
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
        for (var o : orders) {
            if (o.getUser() != null && o.getUser().getAddress() == null)
                o.getUser().setAddress(new Address());
        }
        entityManager.close();
        return orders;
    }

    @Override
    public List<Order> getByColumn(Object value, boolean strict) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Integer insert(Order order) {
        var entityManager = getManager();
        entityManager.getTransaction().begin();

        var u = order.getUser();
        if (u != null) {
            u = entityManager.find(User.class, u.getUserId());
            u.getOrders().add(order);
        }

        var uc = order.getUnregisteredCustomer();
        if (uc != null) {
            uc = entityManager.find(UnregisteredCustomer.class, uc.getUnregisteredCustomerId());
            uc.getOrders().add(order);
        }

        var dt = order.getDeliveryType();
        dt = entityManager.find(DeliveryType.class, dt.getDeliveryTypeId());
        order.setDeliveryType(dt);

        for (var op : order.getOrderProducts())
            op.setOrder(order);

        entityManager.persist(order);

        entityManager.getTransaction().commit();
        entityManager.close();

        return order.getOrderId();
    }

    @Override
    public Integer update(Order order) {
        var entityManager = getManager();

        entityManager.getTransaction().begin();

        var current = entityManager.find(Order.class, order.getOrderId());

        // ORDER PRODUCTS
        for (var op : current.getOrderProducts()) {
            if (!order.getOrderProducts().contains(op)) {
                op.getProduct().getOrderProducts().remove(op);
                op.setOrder(null);
            }
        }

        for (var op : order.getOrderProducts()) {
            if (current.getOrderProducts().contains(op))
                continue;
            entityManager.persist(op);
            op.setOrder(current);
            current.getOrderProducts().add(op);
        }

        current.getOrderProducts().retainAll(order.getOrderProducts());

        // DELIVERY TYPE
        var toDelete = current.getDeliveryType();
        toDelete.getOrders().remove(current);

        var toSetDeliveryType = entityManager.find(
                DeliveryType.class,
                order.getDeliveryType().getDeliveryTypeId()
        );
        toSetDeliveryType.getOrders().add(current);
        current.setDeliveryType(toSetDeliveryType);

        // STATUS
        current.setStatus(order.getStatus());

        // COMPLETE TIME
        current.setCompleteTime(order.getCompleteTime());

        entityManager.getTransaction().commit();
        entityManager.close();
        return order.getOrderId();
    }

    @Override
    public void delete(Integer id) {
        var entityManager = getManager();
        entityManager.getTransaction().begin();

        var order = entityManager.find(Order.class, id);

        // DELIVERY TYPE
        var deliveryType = order.getDeliveryType();
        deliveryType.getOrders().remove(order);

        for (var op : order.getOrderProducts()) {
            op.setOrder(null);
            op.getProduct().getOrderProducts().remove(op);
        }
        entityManager.remove(order);

        entityManager.getTransaction().commit();
        entityManager.close();
    }

    private EntityManager getManager() {
        return entityManagerFactory.createEntityManager();
    }

}
