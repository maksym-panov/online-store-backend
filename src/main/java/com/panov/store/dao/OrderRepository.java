package com.panov.store.dao;

import com.panov.store.model.*;
import com.panov.store.common.Status;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

/**
 * The repository of {@link Order} objects. Implements {@link DAO} interface.
 *
 * @author Maksym Panov
 * @version 1.0
 */
@Repository
public class OrderRepository implements DAO<Order> {
    private final EntityManagerFactory entityManagerFactory;

    @Autowired
    public OrderRepository(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    /**
     * Retrieves an {@link Order} object from the database by its identity.
     *
     * @param id an identifier of the {@link Order} which user wants to retrieve
     * @return an optional of the sought {@link Order} object.
     */
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

    /**
     * Retrieves all the orders from the database.
     *
     * @return a list of all {@link Order} that exist in the database
     */
    @Override
    public List<Order> getAll() {
        var entityManager = getManager();

        List<Order> orders;

        try {
            orders = entityManager
                    .createQuery("select o from Order o", Order.class)
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

    /**
     * Saves new {@link Order} and its {@link OrderProducts} to the database.
     *
     * @param order an entity to save
     * @return an identity of saved {@link Order} object
     */
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

    /**
     * Updates information about {@link Order} object. Deals with {@link OrderProducts}, <br>
     * {@link DeliveryType}, status and completion time of the {@link Order}
     *
     * @param order an object with update information
     * @return an identity of changed {@link Order} object.
     */
    @Override
    public Integer update(Order order) {
        var entityManager = getManager();

        try {
            entityManager.getTransaction().begin();

            var current = entityManager.find(Order.class, order.getOrderId());

            // Remove order products that were deleted by user
            if (order.getOrderProducts() != null && !order.getOrderProducts().isEmpty()) {
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
            }

            // Update delivery type
            if (order.getDeliveryType() != null) {
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
            }

            // Update a total sum
            if (order.getTotal() != null)
                current.setTotal(order.getTotal());

            // Update a status of the order
            if (order.getStatus() != null)
                current.setStatus(order.getStatus());

            // Update a completion time of the order
            if (order.getStatus() == Status.COMPLETED && order.getCompleteTime() == null)
                current.setCompleteTime(new Timestamp(System.currentTimeMillis()));

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

    /**
     * Gets new instance of {@link EntityManager} from {@link EntityManagerFactory} instance.
     *
     * @return an {@link EntityManager} instance
     */
    private EntityManager getManager() {
        return entityManagerFactory.createEntityManager();
    }

}
