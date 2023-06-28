package com.panov.store.dao;

import com.panov.store.model.Address;
import com.panov.store.model.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * The repository of {@link User} objects. Implements {@link DAO} interface.
 *
 * @author Maksym Panov
 * @version 1.0
 */
@Repository
public class UserRepository implements DAO<User> {
    private final EntityManagerFactory entityManagerFactory;

    @Autowired
    public UserRepository(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    /**
     * Retrieves a {@link User} object from the database by its identity.
     *
     * @param id an identifier of the {@link User} which user wants to retrieve
     * @return an optional of sought {@link User}
     */
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

    /**
     * Returns a list of all {@link User} objects
     * that are present in the database.
     *
     * @return a list of all {@link User} objects
     */
    @Override
    public List<User> getAll() {
        var entityManager = getManager();

        List<User> users;

        try {
            users = entityManager
                    .createQuery("select u from User u", User.class)
                    .getResultList();
            for (var u : users)
                if (u != null && u.getAddress() == null)
                    u.setAddress(new Address());
        } finally {
            entityManager.close();
        }

        return users;
    }


    /**
     * Retrieves all {@link User} objects whose phone numbers or emails match provided pattern.
     *
     * @param value a pattern for choosing objects, may be string.
     * @param strict if true, method should search for exact equality and
     *               if false, method should see {@code value} as a part of
     *               phone number or email
     * @return a list of {@link User} objects whose phone numbers or emails match provided value.
     */
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

    /**
     * Created new {@link User} instance and saves it to <br>
     * the database.
     *
     * @param user an {@link User} to save
     * @return an identity of saved {@link User} object
     */
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

    /**
     * Updates information about existing {@link User}.
     *
     * @param patchUser an object with update information.
     * @return an identity of the updated {@link User}
     */
    @Override
    public Integer update(User patchUser) {
        var entityManager = getManager();

        try {
            entityManager.getTransaction().begin();
            User currentUser = entityManager.find(User.class, patchUser.getUserId());

            var pi = patchUser.getPersonalInfo();
            var a = patchUser.getAddress();

            if (patchUser.getAccess() != null)
                currentUser.setAccess(patchUser.getAccess());

            if (pi != null) {
                String pn = pi.getPhoneNumber();
                String e = pi.getEmail();
                String fn = pi.getFirstname();
                String ln = pi.getLastname();

                var currentPi = currentUser.getPersonalInfo();

                currentPi.setPhoneNumber(pn);
                currentPi.setEmail(e);
                currentPi.setFirstname(fn);
                currentPi.setLastname(ln);
            } else {
                currentUser.setPersonalInfo(new User.PersonalInfo());
            }

            if (a != null) {
                String r = a.getRegion();
                String d = a.getDistrict();
                String c = a.getCity();
                String s = a.getStreet();
                Integer b = a.getBuilding();
                Integer ap = a.getApartment();
                Integer pc = a.getPostalCode();

                if (currentUser.getAddress() == null)
                    currentUser.setAddress(new Address());

                var currentA = currentUser.getAddress();

                currentA.setRegion(r);
                currentA.setDistrict(d);
                currentA.setCity(c);
                currentA.setStreet(s);
                currentA.setBuilding(b);
                currentA.setApartment(ap);
                currentA.setPostalCode(pc);
            } else {
                currentUser.setAddress(new Address());
            }

            entityManager.getTransaction().commit();
        } finally {
            entityManager.close();
        }

        return patchUser.getUserId();
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
