package com.panov.store.dao;

import com.panov.store.model.UnregisteredCustomer;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * The repository of {@link UnregisteredCustomer} objects. Implements {@link DAO} interface.
 *
 * @author Maksym Panov
 * @version 1.0
 */
@Repository
public class UnregisteredCustomerRepository implements DAO<UnregisteredCustomer> {
    private final EntityManagerFactory entityManagerFactory;

    @Autowired
    public UnregisteredCustomerRepository(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    /**
     * Retrieves a {@link UnregisteredCustomer} object from the database by its identity.
     *
     * @param id an identifier of the {@link UnregisteredCustomer} which user wants to retrieve
     * @return an optional of sought {@link UnregisteredCustomer}
     */
    @Override
    public Optional<UnregisteredCustomer> get(int id) {
        var entityManager = getManager();

        Optional<UnregisteredCustomer> unregisteredCustomer;

        try {
            unregisteredCustomer = Optional.ofNullable(entityManager.find(UnregisteredCustomer.class, id));
        } finally {
            entityManager.close();
        }

        return unregisteredCustomer;
    }

    /**
     * Returns a list of {@link UnregisteredCustomer} objects
     * that are present in the database regarding offset and quantity.
     *
     * @return a list of all {@link UnregisteredCustomer} objects
     */
    @Override
    public List<UnregisteredCustomer> getPackage(Integer offset, Integer quantity) {
        var entityManager = getManager();

        List<UnregisteredCustomer> unregisteredCustomers;

        if (offset == null || offset < 0)
            offset = 0;
        if (quantity == null || quantity < 0)
            quantity = 500;

        try {
            unregisteredCustomers = entityManager
                            .createQuery(
                                    "select uc from UnregisteredCustomer uc",
                                    UnregisteredCustomer.class
                            )
                            .setFirstResult(offset)
                            .setMaxResults(quantity)
                            .getResultList();
        } finally {
            entityManager.close();
        }

        return unregisteredCustomers;
    }

    @Override
    public List<UnregisteredCustomer> getByColumn(Object value, Integer offset, Integer quantity, boolean strict) {
        throw new UnsupportedOperationException();
    }

    /**
     * Created new {@link UnregisteredCustomer} instance and saves it to <br>
     * the database.
     *
     * @param unregisteredCustomer an {@link UnregisteredCustomer} to save
     * @return an identity of saved {@link UnregisteredCustomer} object
     */
    @Override
    public Integer insert(UnregisteredCustomer unregisteredCustomer) {
        var entityManager = getManager();

        try {
            entityManager.getTransaction().begin();
            entityManager.persist(unregisteredCustomer);
            entityManager.getTransaction().commit();
        } finally {
            entityManager.close();
        }

        return unregisteredCustomer.getUnregisteredCustomerId();
    }

    /**
     * Updates information about existing {@link UnregisteredCustomer}.
     *
     * @param unregisteredCustomer an object with update information.
     * @return an identity of the updated {@link UnregisteredCustomer}
     */
    @Override
    public Integer update(UnregisteredCustomer unregisteredCustomer) {
        var entityManager = getManager();

        try {
            entityManager.getTransaction().begin();
            entityManager.merge(unregisteredCustomer);
            entityManager.getTransaction().commit();
        } finally {
            entityManager.close();
        }

        return unregisteredCustomer.getUnregisteredCustomerId();
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
