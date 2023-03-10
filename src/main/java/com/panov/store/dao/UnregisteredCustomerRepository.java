package com.panov.store.dao;

import com.panov.store.model.UnregisteredCustomer;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class UnregisteredCustomerRepository implements DAO<UnregisteredCustomer> {
    private final EntityManagerFactory entityManagerFactory;

    @Autowired
    public UnregisteredCustomerRepository(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    @Override
    public Optional<UnregisteredCustomer> get(int id) {
        var entityManager = getManager();
        var unregisteredCustomer = Optional.ofNullable(entityManager.find(UnregisteredCustomer.class, id));
        entityManager.close();
        return unregisteredCustomer;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<UnregisteredCustomer> getAll() {
        var entityManager = getManager();
        List<UnregisteredCustomer> unregisteredCustomers =
                (List<UnregisteredCustomer>) entityManager
                        .createQuery("select uc from UnregisteredCustomer uc")
                        .getResultList();
        entityManager.close();
        return unregisteredCustomers;
    }

    @Override
    public List<UnregisteredCustomer> getByColumn(Object value) {
        var entityManager = getManager();
        var customers = entityManager.createQuery("select uc from UnregisteredCustomer uc where uc.phoneNumber = :pn", UnregisteredCustomer.class)
                .setParameter("pn", value)
                .getResultList();
        entityManager.close();
        return customers;
    }

    @Override
    public Integer insert(UnregisteredCustomer unregisteredCustomer) {
        var entityManager = getManager();
        entityManager.getTransaction().begin();
        entityManager.persist(unregisteredCustomer);
        entityManager.getTransaction().commit();
        entityManager.close();
        return unregisteredCustomer.getUnregisteredCustomerId();
    }

    @Override
    public Integer update(UnregisteredCustomer unregisteredCustomer) {
        var entityManager = getManager();
        entityManager.getTransaction().begin();
        entityManager.merge(unregisteredCustomer);
        entityManager.getTransaction().commit();
        entityManager.close();
        return unregisteredCustomer.getUnregisteredCustomerId();
    }

    @Override
    public void delete(Integer id) {
        var entityManager = getManager();
        entityManager.getTransaction().begin();
        entityManager.remove(entityManager.find(UnregisteredCustomer.class, id));
        entityManager.getTransaction().commit();
        entityManager.close();
    }

    private EntityManager getManager() {
        return entityManagerFactory.createEntityManager();
    }
}
