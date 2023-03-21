package com.panov.store.dao;

import com.panov.store.model.Address;
import com.panov.store.model.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class UserRepository implements DAO<User> {
    private final EntityManagerFactory entityManagerFactory;

    @Autowired
    public UserRepository(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    @Override
    public Optional<User> get(int id) {
        var entityManager = getManager();

        Optional<User> user;

        try {
            user = Optional.ofNullable(entityManager.find(User.class, id));
            if (user.isPresent() && user.get().getAddress() == null)
                user.get().setAddress(new Address());
        } finally {
            entityManager.close();
        }

        return user;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<User> getAll() {
        var entityManager = getManager();

        List<User> users;

        try {
            users = (List<User>) entityManager
                    .createQuery("select u from User u")
                    .getResultList();
            for (var u : users)
                if (u != null && u.getAddress() == null)
                    u.setAddress(new Address());
        } finally {
            entityManager.close();
        }

        return users;
    }

    @Override
    public List<User> getByColumn(Object value, boolean strict) {
        var entityManager = getManager();

        List<User> users;

        try {
            String probable = value.toString();
            if (!strict)
                probable = "%" + probable + "%";

            users = entityManager
                    .createQuery("select u from User u where u.personalInfo.phoneNumber like :pn", User.class)
                    .setParameter("pn", probable)
                    .getResultList();
            if (users == null || users.isEmpty()) {
                users = entityManager
                        .createQuery("select u from User u where u.personalInfo.email like :email", User.class)
                        .setParameter("email", probable)
                        .getResultList();
            }
            if (users != null && !users.isEmpty() && users.get(0).getAddress() == null)
                users.get(0).setAddress(new Address());
        } finally {
            entityManager.close();
        }

        return users;
    }

    @Override
    public Integer insert(User user) {
        var entityManager = getManager();

        try {
            entityManager.getTransaction().begin();
            entityManager.persist(user);
            entityManager.getTransaction().commit();
        } finally {
            entityManager.close();
        }

        return user.getUserId();
    }

    @Override
    public Integer update(User user) {
        var entityManager = getManager();

        try {
            entityManager.getTransaction().begin();
            entityManager.merge(user);
            entityManager.getTransaction().commit();
        } finally {
            entityManager.close();
        }

        return user.getUserId();
    }

    @Override
    public void delete(Integer id) {
        throw new UnsupportedOperationException();
    }

    private EntityManager getManager() {
        return entityManagerFactory.createEntityManager();
    }
}
