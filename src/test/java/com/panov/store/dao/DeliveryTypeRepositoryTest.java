package com.panov.store.dao;

import com.panov.store.Utils;
import com.panov.store.model.DeliveryType;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class DeliveryTypeRepositoryTest {
    static EntityManagerFactory entityManagerFactory;

    @BeforeAll
    static void init() {
        entityManagerFactory = Persistence.createEntityManagerFactory("test-db-context");
    }

    @AfterEach
    void cleanDatabase() {
        Utils.cleanDatabase(entityManagerFactory.createEntityManager());
    }

    @AfterAll
    static void purge() {
        entityManagerFactory.close();
    }

    @Test
    void shouldReturnAllExistingDeliveryTypes() {
        var repoTest = new DeliveryTypeRepository(entityManagerFactory);

        var deliveryType1 = new DeliveryType();
        var deliveryType2 = new DeliveryType();
        var deliveryType3 = new DeliveryType();

        deliveryType1.setName("Delivery type #1");
        deliveryType2.setName("Delivery type #2");
        deliveryType3.setName("Delivery type #3");

        repoTest.insert(deliveryType1);
        repoTest.insert(deliveryType2);
        repoTest.insert(deliveryType3);

        var expected = List.of(deliveryType1, deliveryType2, deliveryType3);
        var actual = new ArrayList<>(repoTest.getPackage(null, null));

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void shouldReturnDeliveryTypeById() {
        var repoTest = new DeliveryTypeRepository(entityManagerFactory);

        var deliveryType1 = new DeliveryType();
        var deliveryType2 = new DeliveryType();
        var deliveryType3 = new DeliveryType();

        deliveryType1.setName("Delivery type #1");
        deliveryType2.setName("Delivery type #2");
        deliveryType3.setName("Delivery type #3");

        var id1 = repoTest.insert(deliveryType1);
        var id2 = repoTest.insert(deliveryType2);
        var id3 = repoTest.insert(deliveryType3);

        var actual1 = repoTest.get(id1).get();
        var actual2 = repoTest.get(id2).get();
        var actual3 = repoTest.get(id3).get();

        assertThat(actual1).isEqualTo(deliveryType1);
        assertThat(actual2).isEqualTo(deliveryType2);
        assertThat(actual3).isEqualTo(deliveryType3);
    }

    @Test
    void shouldReturnDeliveryTypeByPartOfItsName() {
        var repoTest = new DeliveryTypeRepository(entityManagerFactory);

        var deliveryType1 = new DeliveryType();
        var deliveryType2 = new DeliveryType();
        var deliveryType3 = new DeliveryType();

        deliveryType1.setName("Delivery type #1");
        deliveryType2.setName("Delivery type #2");
        deliveryType3.setName("Delivery type #3");

        repoTest.insert(deliveryType1);
        repoTest.insert(deliveryType2);
        repoTest.insert(deliveryType3);

        var actual1 = repoTest.getByColumn("1", false);
        var actual2 = repoTest.getByColumn("2", false);
        var actual3 = repoTest.getByColumn("3", false);

        assertThat(actual1.get(0)).isEqualTo(deliveryType1);
        assertThat(actual2.get(0)).isEqualTo(deliveryType2);
        assertThat(actual3.get(0)).isEqualTo(deliveryType3);
    }

    @Test
    void shouldChangeNameOfTheDeliveryType() {
        var repoTest = new DeliveryTypeRepository(entityManagerFactory);

        var deliveryType1 = new DeliveryType();
        var deliveryType2 = new DeliveryType();
        var deliveryType3 = new DeliveryType();

        deliveryType1.setName("Delivery type #1");
        deliveryType2.setName("Delivery type #2");
        deliveryType3.setName("Delivery type #3");

        var id1 = repoTest.insert(deliveryType1);
        var id2 = repoTest.insert(deliveryType2);
        var id3 = repoTest.insert(deliveryType3);

        var inserted1 = repoTest.get(id1).get();
        var inserted2 = repoTest.get(id2).get();
        var inserted3 = repoTest.get(id3).get();

        inserted1.setName("NEW NAME 1");
        inserted2.setName("NEW NAME 2");
        inserted3.setName("NEW NAME 3");

        var upId1 = repoTest.update(inserted1);
        var upId2 = repoTest.update(inserted2);
        var upId3 = repoTest.update(inserted3);

        var updated1 = repoTest.get(upId1);
        var updated2 = repoTest.get(upId2);
        var updated3 = repoTest.get(upId3);

        assertThat(updated1.get()).isEqualTo(inserted1);
        assertThat(updated2.get()).isEqualTo(inserted2);
        assertThat(updated3.get()).isEqualTo(inserted3);
    }

    @Test
    void shouldDeleteDeliveryType() {
        var repoTest = new DeliveryTypeRepository(entityManagerFactory);

        var deliveryType1 = new DeliveryType();
        var deliveryType2 = new DeliveryType();
        var deliveryType3 = new DeliveryType();

        deliveryType1.setName("Delivery type #1");
        deliveryType2.setName("Delivery type #2");
        deliveryType3.setName("Delivery type #3");

        var id1 = repoTest.insert(deliveryType1);
        repoTest.insert(deliveryType2);
        var id3 = repoTest.insert(deliveryType3);

        var expected1 = List.of(deliveryType1, deliveryType2, deliveryType3);
        var actual1 = new ArrayList<>(repoTest.getPackage(null, null));

        repoTest.delete(id1);
        repoTest.delete(id3);

        var expected2 = List.of(deliveryType2);
        var actual2 = new ArrayList<>(repoTest.getPackage(null, null));

        assertThat(actual1).isEqualTo(expected1);
        assertThat(actual2).isEqualTo(expected2);
    }
}
