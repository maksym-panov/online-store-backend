package com.panov.store.dao;

import com.panov.store.Utils;
import com.panov.store.model.Address;
import com.panov.store.model.UnregisteredCustomer;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;

public class UnregisteredCustomerRepositoryTest {
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
    void shouldAddUnregisteredCustomerToDatabaseAndReturnActualIdAndThenReturnListOfThemAll() {
        // given
        var repoTest = new UnregisteredCustomerRepository(entityManagerFactory);
        // when
        var uc1 = new UnregisteredCustomer();
        var uc2 = new UnregisteredCustomer();

        var a1 = new Address();
        var a2 = new Address();

        a1.setRegion("Kharkiv");
        a1.setDistrict("Chuguiv");
        a1.setCity("Chuguiv");
        a1.setStreet("Shevchenka");
        a1.setBuilding(100);
        a1.setApartment(32);
        a1.setPostalCode(63531);

        uc1.setPhoneNumber("0964264321");
        uc1.setFirstname("Maksym");
        uc1.setLastname("Panov");
        uc1.setAddress(a1);

        a2.setRegion("Kyiv");
        a2.setCity("Kyiv");
        a2.setStreet("Franka");
        a2.setBuilding(32);
        a2.setPostalCode(1001);

        uc2.setPhoneNumber("0994824641");
        uc2.setFirstname("Angelo");
        uc2.setAddress(a2);

        var id1 = repoTest.insert(uc1);
        var id2 = repoTest.insert(uc2);

        var actual1 = repoTest.get(id1);
        var actual2 = repoTest.get(id2);

        var expectedList = List.of(uc1, uc2);
        var actualList = repoTest.getAll();

        // then
        assertThat(id1).isNotNull();
        assertThat(actual1).isNotNull();
        assertThat(actual1.isPresent()).isTrue();
        assertThat(actual1.get()).isEqualTo(uc1);

        assertThat(id2).isNotNull();
        assertThat(actual2).isNotNull();
        assertThat(actual2.isPresent()).isTrue();
        assertThat(actual2.get()).isEqualTo(uc2);

        assertThat(actualList).isEqualTo(expectedList);
    }

    @Test
    void shouldThrowIfUsingFindByColumn() {
        // given
        var repoTest = new UnregisteredCustomerRepository(entityManagerFactory);

        // then
        assertThatExceptionOfType(UnsupportedOperationException.class)
                .isThrownBy(() -> repoTest.getByColumn(new Object()));
    }

    @Test
    void shouldUpdateFieldsInDatabase() {
        // given
        var repoTest = new UnregisteredCustomerRepository(entityManagerFactory);

        var uc1 = new UnregisteredCustomer();
        var uc2 = new UnregisteredCustomer();

        var a1 = new Address();
        var a2 = new Address();

        a1.setRegion("Kharkiv");
        a1.setDistrict("Chuguiv");
        a1.setCity("Chuguiv");
        a1.setStreet("Shevchenka");
        a1.setBuilding(100);
        a1.setApartment(32);
        a1.setPostalCode(63531);

        uc1.setPhoneNumber("0964264321");
        uc1.setFirstname("Maksym");
        uc1.setLastname("Panov");
        uc1.setAddress(a1);

        a2.setRegion("Kyiv");
        a2.setCity("Kyiv");
        a2.setStreet("Franka");
        a2.setBuilding(32);
        a2.setPostalCode(1001);

        uc2.setPhoneNumber("0994824641");
        uc2.setFirstname("Angelo");
        uc2.setAddress(a2);

        // when
        repoTest.insert(uc1);
        repoTest.insert(uc2);

        uc1.setPhoneNumber("0000000000");
        uc1.setFirstname("test1");
        uc1.setLastname("test1");
        uc1.getAddress().setRegion("test1");
        uc1.getAddress().setDistrict("test1");
        uc1.getAddress().setCity("test1");
        uc1.getAddress().setStreet("test1");
        uc1.getAddress().setBuilding(1);
        uc1.getAddress().setApartment(1);
        uc1.getAddress().setPostalCode(11111);

        uc2.setPhoneNumber("0111111111");
        uc2.setFirstname("test2");
        uc2.setLastname("test2");
        uc2.getAddress().setRegion("test2");
        uc2.getAddress().setDistrict("test2");
        uc2.getAddress().setCity("test2");
        uc2.getAddress().setStreet("test2");
        uc2.getAddress().setBuilding(2);
        uc2.getAddress().setApartment(2);
        uc2.getAddress().setPostalCode(22222);

        var id1 = repoTest.update(uc1);
        var id2 = repoTest.update(uc2);

        var up1 = repoTest.get(id1);
        var up2 = repoTest.get(id2);

        // then
        assertThat(id1).isNotNull();
        assertThat(up1).isNotNull();
        assertThat(up1.isPresent()).isTrue();
        assertThat(up1.get()).isEqualTo(uc1);

        assertThat(id2).isNotNull();
        assertThat(up2).isNotNull();
        assertThat(up2.isPresent()).isTrue();
        assertThat(up2.get()).isEqualTo(uc2);
    }

    @Test
    void shouldDeleteFromDatabase() {
        // given
        var repoTest = new UnregisteredCustomerRepository(entityManagerFactory);

        var uc1 = new UnregisteredCustomer();
        var uc2 = new UnregisteredCustomer();

        var a1 = new Address();
        var a2 = new Address();

        a1.setRegion("Kharkiv");
        a1.setDistrict("Chuguiv");
        a1.setCity("Chuguiv");
        a1.setStreet("Shevchenka");
        a1.setBuilding(100);
        a1.setApartment(32);
        a1.setPostalCode(63531);

        uc1.setPhoneNumber("0964264321");
        uc1.setFirstname("Maksym");
        uc1.setLastname("Panov");
        uc1.setAddress(a1);

        a2.setRegion("Kyiv");
        a2.setCity("Kyiv");
        a2.setStreet("Franka");
        a2.setBuilding(32);
        a2.setPostalCode(1001);

        uc2.setPhoneNumber("0994824641");
        uc2.setFirstname("Angelo");
        uc2.setAddress(a2);

        // when
        var id1 = repoTest.insert(uc1);
        var id2 = repoTest.insert(uc2);

        var expectedBeforeDeletion = List.of(uc1, uc2);
        var actualBeforeDeletion = repoTest.getAll();

        repoTest.delete(id2);
        var expectedAfterFirstDeletion = List.of(uc1);
        var actualAfterFirstDeletion = repoTest.getAll();

        repoTest.delete(id1);
        var expectedAfterSecondDeletion = Collections.emptyList();
        var actualAfterSecondDeletion = repoTest.getAll();

        // then
        assertThat(actualBeforeDeletion).isEqualTo(expectedBeforeDeletion);
        assertThat(actualAfterFirstDeletion).isEqualTo(expectedAfterFirstDeletion);
        assertThat(actualAfterSecondDeletion).isEqualTo(expectedAfterSecondDeletion);
    }
}
