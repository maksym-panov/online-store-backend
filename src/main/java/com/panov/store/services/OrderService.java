package com.panov.store.services;

import com.panov.store.dao.DAO;
import com.panov.store.dao.OrderRepository;
import com.panov.store.exceptions.ResourceNotCreatedException;
import com.panov.store.exceptions.ResourceNotFoundException;
import com.panov.store.exceptions.ResourceNotUpdatedException;
import com.panov.store.model.*;
import com.panov.store.common.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Collections;
import java.util.List;

/**
 * Service-layer class that processes {@link Order} entities.
 *
 * @author Maksym Panov
 * @version 1.0
 * @see DAO
 * @see OrderRepository
 * @see UserService
 * @see UnregisteredCustomerService
 */
@Service
public class OrderService {
    private final DAO<Order> repository;
    private final UserService userService;
    private final UnregisteredCustomerService unregCustService;

    @Autowired
    public OrderService(DAO<Order> repository, UserService userService,
                        UnregisteredCustomerService unregCustService) {
        this.repository = repository;
        this.userService = userService;
        this.unregCustService = unregCustService;
    }

    /**
     * Uses {@link DAO} implementation to retrieve list of all existing {@link Order} entities. <br><br>
     * Only {@link User} with {@code Access.ADMINISTRATOR} or {@code Access.MANAGER} authority
     * can invoke this method. <br><br>
     * Re-throws a {@link ResourceNotFoundException} if {@link DAO} object throws an exception.
     *
     * @return a {@link List} of {@link Order} objects
     */
    @PreAuthorize("hasAuthority('ADMINISTRATOR') or hasAuthority('MANAGER')")
    public List<Order> getOrdersList(Integer offset, Integer quantity) {
        try {
            var list = repository.getPackage(offset, quantity);
            if (list == null)
                return Collections.emptyList();

            return list;
        } catch(Exception e) {
            e.printStackTrace();
            throw new ResourceNotFoundException("Could not find products");
        }
    }

    /**
     * Uses {@link DAO} implementation to retrieve a {@link Order} entity by specified identity. <br><br>
     * Only {@link User} with {@code Access.ADMINISTRATOR} or {@code Access.MANAGER} authority
     * can invoke this method. <br><br>
     * Re-throws a {@link ResourceNotFoundException} if {@link DAO} object throws an exception or there is
     * no such {@link Order} object.
     *
     * @param id an identity of the sought {@link Order}
     * @return a {@link Order} object with specified identity
     */
    @PreAuthorize("hasAuthority('ADMINISTRATOR') or hasAuthority('MANAGER')")
    public Order getById(Integer id) {
        return repository.get(id).orElseThrow(() ->
                new ResourceNotFoundException("Could not find this order")
        );
    }

    /**
     * Uses {@link UserService} to find {@link Order}s of {@link User} with specified identity. <br><br>
     * Only {@link User} with {@code Access.ADMINISTRATOR} or {@code Access.MANAGER} authority or the
     * {@link User} that is the owner of provided identity, can invoke this method. <br><br>
     * Re-throws a {@link ResourceNotFoundException} if {@link DAO} object throws an exception or there is
     * no such {@link Order} object.
     *
     * @param userId an identity of the {@link User} whose {@link Order}s are being searched
     * @return a {@link List} of {@link Order}s of the {@link User} with specified identity
     */
    @PreAuthorize(
            "hasAuthority('ADMINISTRATOR') or " +
            "hasAuthority('MANAGER') or " +
            "hasAuthority(#userId.toString())"
    )
    public List<Order> getOrdersByUser(Integer userId) {
        User user = userService.getById(userId);
        var list = user.getOrders();
        if (list == null)
            return Collections.emptyList();

        return list;
    }

    /**
     * Uses {@link UnregisteredCustomerService} to find {@link Order}s of {@link UnregisteredCustomer}
     * with specified identity. <br><br>
     * Only {@link User} with {@code Access.ADMINISTRATOR} or {@code Access.MANAGER} authority
     * can invoke this method. <br><br>
     * Re-throws a {@link ResourceNotFoundException} if {@link DAO} object throws an exception or there is
     * no such {@link Order} object.
     *
     * @param unregCustId an identity of the {@link UnregisteredCustomer} whose {@link Order}s are being searched
     * @return a {@link List} of {@link Order}s of the {@link UnregisteredCustomer} with specified identity
     */
    @PreAuthorize("hasAuthority('ADMINISTRATOR') or hasAuthority('MANAGER')")
    public List<Order> getOrdersByUnregisteredCustomer(Integer unregCustId) {
        UnregisteredCustomer customer = unregCustService.getById(unregCustId);
        var list = customer.getOrders();
        if (list == null)
            return Collections.emptyList();

        return list;
    }

    /**
     * Uses {@link DAO} implementation to save new {@link Order} in the data storage. <br><br>
     * A {@link User} with {@code Access.ADMINISTRATOR} or {@code Access.MANAGER} authority
     * can create {@link Order} without restrictions. If {@link Order} is being created by
     * unregistered customer, it should contain all required information about {@link UnregisteredCustomer}
     * and no information about {@link User}; or if it is being created by {@link User}, then specified {@link Order} object should contain
     * {@code user} field with {@code userId} which is equal to identity of this {@link User}<br><br>
     * Re-throws a {@link ResourceNotCreatedException} if {@link DAO} object throws an exception.
     *
     * @param order an object to save
     * @return an identity of saved {@link Order}
     */
    @PreAuthorize("" +
            "(#order.user != null and hasAuthority(#order.user.userId.toString())) or " +
            "#order.unregisteredCustomer != null or " +
            "hasAuthority('ADMINISTRATOR') or " +
            "hasAuthority('MANAGER')"
    )
    public Integer createOrder(Order order) {
        // Orders with simultaneously specified User and UnregisteredCustomer
        // are not allowed, such as well as Orders without owner
        if (order.getUser() == null && order.getUnregisteredCustomer() == null ||
            order.getUser() != null && order.getUnregisteredCustomer() != null)
            throw new ResourceNotCreatedException("Could not post this order");

        Integer id = null;

        validateData(order, Operation.CREATION);

        order.setPostTime(new Timestamp(System.currentTimeMillis() - 10000));
        order.setStatus(Status.POSTED);

        try {
            if (order.getUnregisteredCustomer() != null)
                unregCustService.createUnregisteredCustomer(order.getUnregisteredCustomer());
            id = repository.insert(order);
        } catch(Exception e) {
            e.printStackTrace();
        }

        if (id == null)
            throw new ResourceNotCreatedException("Could not post this order");

        return id;
    }

    /**
     * Uses {@link DAO} implementation to change information of {@link Order}. <br><br>
     * Only {@link User} with {@code Access.ADMINISTRATOR} or {@code Access.MANAGER} authority
     * can invoke this method. <br><br>
     * Re-throws a {@link ResourceNotUpdatedException} if {@link DAO} object throws an exception.
     *
     * @param order an object that contains new data and identity of {@link Order}
     *                     that should be updated
     * @return an identity of updated {@link Order}
     */
    @PreAuthorize("hasAuthority('ADMINISTRATOR') or hasAuthority('MANAGER')")
    public Integer changeOrder(Order order) {
        Integer id = null;

        validateData(order, Operation.UPDATE);

        try {
            id = repository.update(order);
        } catch(Exception e) {
            e.printStackTrace();
        }

        if (id == null)
            throw new ResourceNotUpdatedException("Could not change this order");

        return id;
    }

    private void validateData(Order order, Operation operation) {
        if (order.getOrderProducts() == null || order.getOrderProducts().isEmpty()) {
            choseErrorMessage(operation);
        }

        var orderProducts = order
                .getOrderProducts()
                .stream()
                .filter(op -> op.getProduct() != null &&
                        op.getProduct().getProductId() != null &&
                        op.getQuantity() != null &&
                        op.getQuantity() > 0
                )
                .toList();

        if (orderProducts.isEmpty()) {
            choseErrorMessage(operation);
        }

        var distinctTestSize = orderProducts.stream()
                .map(op -> op.getProduct().getProductId())
                .distinct()
                .count();

        if (orderProducts.size() != distinctTestSize) {
            choseErrorMessage(operation);
        }

        order.setOrderProducts(orderProducts);
    }

    private void choseErrorMessage(Operation operation) {
        switch (operation) {
            case UPDATE -> throw new ResourceNotUpdatedException("Could not change this order.");
            case CREATION -> throw new ResourceNotCreatedException("Could not post this order.");
        }
    }

    private enum Operation {
        CREATION,
        UPDATE
    }
}
