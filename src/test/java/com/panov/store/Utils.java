package com.panov.store;

import jakarta.persistence.EntityManager;

public final class Utils {
    public static void cleanDatabase(EntityManager em) {
        em.getTransaction().begin();
        em.createNativeQuery("DELETE FROM OrderProducts").executeUpdate();
        em.createNativeQuery("DELETE FROM \"Order\"").executeUpdate();
        em.createNativeQuery("DELETE FROM DeliveryType").executeUpdate();
        em.createNativeQuery("DELETE FROM Product_ProductType").executeUpdate();
        em.createNativeQuery("DELETE FROM ProductType").executeUpdate();
        em.createNativeQuery("DELETE FROM Product").executeUpdate();
        em.createNativeQuery("DELETE FROM \"User\"").executeUpdate();
        em.createNativeQuery("DELETE FROM UnregisteredCustomer").executeUpdate();
        em.getTransaction().commit();
    }
}
