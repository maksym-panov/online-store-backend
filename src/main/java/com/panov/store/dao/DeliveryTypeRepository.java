package com.panov.store.dao;

import com.panov.store.model.DeliveryType;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class DeliveryTypeRepository implements DAO<DeliveryType> {
    private final EntityManagerFactory entityManagerFactory;

    @Autowired
    public DeliveryTypeRepository(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

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

    @Override
    public List<DeliveryType> getByColumn(Object value, boolean strict) {
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

    private EntityManager getManager() {
        return entityManagerFactory.createEntityManager();
    }
}
