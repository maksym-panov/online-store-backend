package com.panov.store.dao;

import com.panov.store.model.DeliveryType;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * The repository of {@link DeliveryType} objects. Implements {@link DAO} interface.
 *
 * @author Maksym Panov
 * @version 1.0
 */
@Repository
public class DeliveryTypeRepository implements DAO<DeliveryType> {
    private final EntityManagerFactory entityManagerFactory;

    @Autowired
    public DeliveryTypeRepository(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    /**
     * Retrieves a {@link DeliveryType} object from the database by its identity.
     *
     * @param id an identifier of the {@link DeliveryType} which user wants to retrieve
     * @return an optional of sought {@link DeliveryType}
     */
    @Override
    public Optional<DeliveryType> get(int id) {
        var entityManager = getManager();

        Optional<DeliveryType> deliveryType;

        try {
            deliveryType = Optional.ofNullable(entityManager.find(DeliveryType.class, id));
        } finally {
            entityManager.close();
        }
        return deliveryType;
    }

    /**
     * Returns a list of all {@link DeliveryType} objects
     * that are present in the database.
     *
     * @return a list of all {@link DeliveryType} objects
     */
    @Override
    public List<DeliveryType> getAll() {
        var entityManager = getManager();

        List<DeliveryType> list;

        try {
            list = entityManager.createQuery("select dt from DeliveryType dt", DeliveryType.class)
                    .getResultList();
        } finally {
            entityManager.close();
        }
        return list;
    }

    /**
     * Retrieves all {@link DeliveryType} objects whose match provided pattern.
     *
     * @param value a pattern for choosing objects, may be string.
     * @param strict if true, method should search for exact equality and
     *               if false, method should see {@code value} as a part of
     *               object field (e.g. part of name)
     * @return a list of {@link DeliveryType} objects whose names match provided value.
     */
    @Override
    public List<DeliveryType> getByColumn(Object value, boolean strict) {
        if (value == null || value.toString().isBlank()) {
            return Collections.emptyList();
        }

        var entityManager = getManager();

        List<DeliveryType> deliveryTypes;

        try {
            String probablyName = value.toString();

            if (!strict)
                probablyName = "%" + probablyName + "%";

            deliveryTypes =
                    entityManager.createQuery("select dt from DeliveryType dt where lower(name) like lower(:name)", DeliveryType.class)
                            .setParameter("name", probablyName)
                            .getResultList();
        } finally {
            entityManager.close();
        }

        return deliveryTypes;
    }

    /**
     * Created new {@link DeliveryType} instance and saves it to <br>
     * the database.
     *
     * @param deliveryType an {@link DeliveryType} to save
     * @return an identity of saved {@link DeliveryType} object
     */
    @Override
    public Integer insert(DeliveryType deliveryType) {
        var entityManager = getManager();

        try {
            entityManager.getTransaction().begin();
            entityManager.persist(deliveryType);
            entityManager.getTransaction().commit();
        } finally {
            entityManager.close();
        }
        return deliveryType.getDeliveryTypeId();
    }

    /**
     * Updates information about existing {@link DeliveryType}.
     *
     * @param deliveryType an object with update information.
     * @return an identity of the updated {@link DeliveryType}
     */
    @Override
    public Integer update(DeliveryType deliveryType) {
        var entityManager = getManager();

        try {
            entityManager.getTransaction().begin();
            entityManager.merge(deliveryType);
            entityManager.getTransaction().commit();
        } finally {
            entityManager.close();
        }
        return deliveryType.getDeliveryTypeId();
    }

    /**
     * Deletes existing {@link DeliveryType} from the database by its identity.
     *
     * @param id an identity of the {@link DeliveryType} to be deleted
     */
    @Override
    public void delete(Integer id) {
        var entityManager = getManager();

        try {
            entityManager.getTransaction().begin();

            // Deleting from all the orders
            var deliveryType = entityManager.find(DeliveryType.class, id);
            for (var o : deliveryType.getOrders()) {
                o.setDeliveryType(null);
            }

            // Deleting actual delivery type from persistence
            entityManager.remove(deliveryType);

            entityManager.getTransaction().commit();
        } finally {
            entityManager.close();
        }
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
