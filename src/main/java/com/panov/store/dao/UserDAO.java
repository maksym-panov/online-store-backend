package com.panov.store.dao;

import com.panov.store.exception.BadRequestException;
import com.panov.store.model.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class UserDAO implements DAO<User> {
    private final EntityManagerFactory entityManagerFactory;

    @Autowired
    public UserDAO(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    @Override
    public Optional<User> get(int id) {
        var entityManager = getManager();
        var user = Optional.ofNullable(entityManager.find(User.class, id));
        entityManager.close();
        return user;
    }

    @Override
    public Optional<User> getByColumn(String naturalId, String value) {
        var entityManager = getManager();
        Optional<User> user;
        try {
            user = Optional.ofNullable((User) entityManager
                    .createQuery("select u from User u where :col = :val")
                    .setParameter("col", naturalId)
                    .setParameter("val", value)
                    .getSingleResult()
            );
        } catch (Exception exception) {
            throw new BadRequestException("Incorrect query", exception);
        }
        entityManager.close();
        return user;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<User> getAll() {
        var entityManager = getManager();
        List<User> users = (List<User>) entityManager
                .createQuery("select u from User u")
                .getResultList();
        entityManager.close();
        return users;
    }

    @Override
    public Integer insert(User user) {
        var entityManager = getManager();
        try {
            entityManager.persist(user);
            entityManager.close();
            return user.getUserId();
        } catch(Exception exception) {
            throw new BadRequestException("Impossible to add new user", exception);
        }
    }

    @Override
    public Integer update(User user) {
        var entityManager = getManager();
        try {
            entityManager.merge(user);
            entityManager.close();
            return user.getUserId();
        } catch(Exception exception) {
            throw new BadRequestException("Impossible to update user", exception);
        }
    }

    @Override
    public void delete(User user) {
        var entityManager = getManager();
        entityManager.remove(user);
        entityManager.close();
    }

    private EntityManager getManager() {
        return entityManagerFactory.createEntityManager();
    }
}
