package com.panov.store.dao;

import com.panov.store.exceptions.ResourceNotUpdatedException;
import com.panov.store.model.*;
import com.panov.store.common.Status;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.TypedQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
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
     * Retrieves orders from the database regarding offset and quantity.
     *
     * @return a list of all {@link Order} that exist in the database
     */
    @Override
    public List<Order> getPackage(Integer offset, Integer quantity) {
        var entityManager = getManager();

        List<Order> orders;

        if (offset == null || offset < 0)
            offset = 0;
        if (quantity == null || quantity < 0)
            quantity = 500;

        try {
            orders = entityManager
                    .createQuery("select o from Order o order by postTime desc", Order.class)
                    .setFirstResult(offset)
                    .setMaxResults(quantity)
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

    public List<Order> getPackageOrderedWithParam(Integer offset, Integer quantity, String order, Status status) {
        var entityManager = getManager();

        if (offset == null || offset < 0)
            offset = 0;
        if (quantity == null || quantity < 0)
            quantity = 500;

        try {

            String query;
            int params;
            if (status != null) {
                params = 1;
                if (order != null) {
                   if (order.equals("asc")) {
                       query = "select o from Order o where o.status = ?1 order by postTime asc";
                   } else {
                        query = "select o from Order o where o.status = ?1 order by postTime desc";
                   }
                } else {
                    query = "select o from Order o where o.status = ?1 order by postTime desc";
                }
            } else {
                params = 0;
                if (order != null) {
                    if (order.equals("asc")) {
                        query = "select o from Order o order by postTime asc";
                    } else {
                        query = "select o from Order o order by postTime desc";
                    }
                } else {
                    query = "select o from Order o order by postTime desc";
                }
            }

            TypedQuery<Order> q = entityManager
                    .createQuery(query, Order.class);

            if (params == 1) {
                q.setParameter(1, status);
            }

            return q.setFirstResult(offset)
                    .setMaxResults(quantity)
                    .getResultList();

        } finally {
            entityManager.close();
        }
    }

    @Override
    public List<Order> getByColumn(Object value, Integer offset, Integer quantity, boolean strict) {
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
            for (var op : order.getOrderProducts()) {
                var productId = op.getProduct().getProductId();
                op.setProduct(
                        entityManager.find(
                                Product.class, productId
                        )
                );

                op.setOrder(order);
            }

            manageProductStocks(order);
            calculateSumsInOrderProducts(order);
            calculateTotal(order);

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
     * @param newData an object with update information
     * @return an identity of changed {@link Order} object.
     */
    @Override
    public Integer update(Order newData) {
        var entityManager = getManager();

        try {
            entityManager.getTransaction().begin();

            var currentOrder = entityManager.find(Order.class, newData.getOrderId());

            // Change product status and complete time
            if (
                    currentOrder.getStatus() != Status.COMPLETED &&
                    currentOrder.getStatus() != Status.ABOLISHED &&
                    newData.getStatus() != null
            ) {
                currentOrder.setStatus(newData.getStatus());

                if (currentOrder.getStatus() == Status.COMPLETED) {
                    currentOrder.setCompleteTime(new Timestamp(System.currentTimeMillis() - 10000));
                }
            }

            // Change the delivery type
            if (
                    newData.getDeliveryType() != null &&
                    newData.getDeliveryType().getDeliveryTypeId() != null
            ) {
                var newDeliveryTypeId = newData.getDeliveryType().getDeliveryTypeId();
                var newDeliveryType = entityManager.find(DeliveryType.class, newDeliveryTypeId);

                newDeliveryType.getOrders().add(currentOrder);
                currentOrder.setDeliveryType(newDeliveryType);
            }

            // If customer abolishes the order, all the products from this order
            // should be returned to the warehouse
            if (currentOrder.getStatus() == Status.ABOLISHED) {
                resetProductStocks(currentOrder);
            }

            // Change the list of ordered products
            if (
                    currentOrder.getStatus() != Status.SHIPPING &&
                    currentOrder.getStatus() != Status.DELIVERED &&
                    currentOrder.getStatus() != Status.COMPLETED &&
                    currentOrder.getStatus() != Status.ABOLISHED
            ) {
                var newOrderProducts = newData.getOrderProducts();
                var currentOrderProducts = currentOrder.getOrderProducts();

                for (var currentOrderProduct : currentOrderProducts) {
                    var productId = currentOrderProduct.getProduct().getProductId();
                    var product = entityManager.find(Product.class, productId);
                    currentOrderProduct.setProduct(product);
                }

                resetProductStocks(currentOrder);

                for (var currentOrderProduct : currentOrderProducts) {
                    currentOrderProduct.setOrder(null);
                    currentOrderProduct.setProduct(null);

                }

                currentOrderProducts.clear();

                currentOrderProducts.addAll(newOrderProducts);

                for (var currentOrderProduct : currentOrderProducts) {
                    var productId = currentOrderProduct.getProduct().getProductId();
                    currentOrderProduct.setProduct(
                            entityManager.find(
                                    Product.class, productId
                            )
                    );

                    currentOrderProduct.setOrder(currentOrder);
                }

                manageProductStocks(currentOrder);
                calculateSumsInOrderProducts(currentOrder);
                calculateTotal(currentOrder);
            }

            entityManager.getTransaction().commit();
        } finally {
            entityManager.close();
        }

        return newData.getOrderId();
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

    /**
     * Gets an order {@link Order} and for each {@link OrderProducts} object inside
     * evaluates new stock of product and saves changes in the database.
     *
     * @param order an order to process
     */
    private void manageProductStocks(Order order) {
        var orderProducts = order.getOrderProducts();

        for (var op : orderProducts) {
            var currentProductStock = op.getProduct().getStock();
            var quantityToSell = op.getQuantity();

            if (currentProductStock < quantityToSell) {
                throw new ResourceNotUpdatedException("Could not update this order.");
            }

            var newProductStock = currentProductStock - quantityToSell;

            op.getProduct().setStock(newProductStock);
        }
    }

    /**
     * Gets an {@link Order} object and 'returns' all ordered product quantities back to the product stock.
     *
     * @param order an order to process
     */
    private void resetProductStocks(Order order) {
        for (var op : order.getOrderProducts()) {
            var currentProductStock = op.getProduct().getStock();
            var quantityFromOrderProduct = op.getQuantity();

            var newProductStock = currentProductStock + quantityFromOrderProduct;
            op.getProduct().setStock(newProductStock);
        }
    }

    /**
     * Calculates prices of different {@link Product}s of the specified {@link Order}. <br><br>
     * {@code sum_i = product_i_price * product_i_quantity} <br><br>
     *
     * @param order an {@link Order} whose {@link OrderProducts} should be processed
     */
    private void calculateSumsInOrderProducts(Order order) {
        for (var op : order.getOrderProducts()) {
            var productPrice = op.getProduct().getPrice();
            var quantity =  BigDecimal.valueOf(op.getQuantity());

            var sum = productPrice.multiply(quantity);

            op.setSum(sum);
        }
    }

    /**
     * Calculates total price of the {@link Order} depending on values of {@code sum}
     * field of the {@link OrderProducts} in the specified {@link Order} object.
     *
     * @param order an {@link Order} whose total price should be calculated
     */
    private void calculateTotal(Order order) {
        BigDecimal total = BigDecimal.ZERO;

        for (var op : order.getOrderProducts()) {
            total = total.add(op.getSum());
        }

        order.setTotal(total);
    }
}
