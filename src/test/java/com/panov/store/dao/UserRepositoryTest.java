package com.panov.store.dao;

import com.panov.store.Utils;
import com.panov.store.model.Address;
import com.panov.store.model.User;
import com.panov.store.utils.Access;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import java.util.Collections;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class UserRepositoryTest {
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
    void shouldAddUsersToDatabaseAndReturnActualId() {
        // given
        var repoTest = new UserRepository(entityManagerFactory);

        var u1 = new User();
        var u2 = new User();

        var pi1 = new User.PersonalInfo();
        var pi2 = new User.PersonalInfo();

        var a1 = new Address();
        var a2 = new Address();

        pi1.setPhoneNumber("0994824689");
        pi1.setFirstname("Dmytro");

        u1.setPersonalInfo(pi1);
        u1.setAddress(a1);
        u1.setAccess(Access.ADMINISTRATOR);

        pi2.setPhoneNumber("0964269257");
        pi2.setEmail("1pan.maxim.52@gmail.com");
        pi2.setFirstname("Maksym");
        pi2.setLastname("Panov");

        a2.setRegion("Kharkiv");
        a2.setDistrict("Chuguiv");
        a2.setCity("Korobochkine");
        a2.setStreet("Shevchenka");
        a2.setBuilding(218);
        a2.setPostalCode(63540);

        u2.setPersonalInfo(pi2);
        u2.setAddress(a2);
        u2.setAccess(Access.USER);


        // when
        var id1 = repoTest.insert(u1);
        var id2 = repoTest.insert(u2);

        var actual1 = repoTest.get(id1);
        var actual2 = repoTest.get(id2);

        // then
        assertThat(id1).isNotNull();
        assertThat(actual1).isNotNull();
        assertThat(actual1.isPresent()).isTrue();
        assertThat(actual1.get()).isEqualTo(u1);
        assertThat(actual1.get().getPersonalInfo()).isEqualTo(u1.getPersonalInfo());
        assertThat(actual1.get().getAddress()).isEqualTo(u1.getAddress());
        assertThat(actual1.get().getAccess()).isEqualTo(u1.getAccess());

        assertThat(id2).isNotNull();
        assertThat(actual2).isNotNull();
        assertThat(actual2.isPresent()).isTrue();
        assertThat(actual2.get()).isEqualTo(u2);
        assertThat(actual2.get().getPersonalInfo()).isEqualTo(u2.getPersonalInfo());
        assertThat(actual2.get().getAddress()).isEqualTo(u2.getAddress());
        assertThat(actual2.get().getAccess()).isEqualTo(u2.getAccess());
    }

    @Test
    void shouldReturnAllUsers() {
        // given
        var repoTest = new UserRepository(entityManagerFactory);

        var u1 = new User();
        var u2 = new User();
        var u3 = new User();

        var pi1 = new User.PersonalInfo();
        var pi2 = new User.PersonalInfo();
        var pi3 = new User.PersonalInfo();

        var a1 = new Address();
        var a2 = new Address();
        var a3 = new Address();

        pi1.setPhoneNumber("0994824689");
        pi1.setFirstname("Dmytro");

        u1.setPersonalInfo(pi1);
        u1.setAddress(a1);
        u1.setAccess(Access.ADMINISTRATOR);

        pi2.setPhoneNumber("0964269257");
        pi2.setEmail("1pan.maxim.52@gmail.com");
        pi2.setFirstname("Maksym");
        pi2.setLastname("Panov");

        a2.setRegion("Kharkiv");
        a2.setDistrict("Chuguiv");
        a2.setCity("Korobochkine");
        a2.setStreet("Shevchenka");
        a2.setBuilding(218);
        a2.setPostalCode(63540);

        u2.setPersonalInfo(pi2);
        u2.setAddress(a2);
        u2.setAccess(Access.USER);

        pi3.setPhoneNumber("0964269111");

        u3.setPersonalInfo(pi3);
        u3.setAddress(a3);
        u3.setAccess(Access.MANAGER);


        // when
        repoTest.insert(u1);
        repoTest.insert(u2);
        repoTest.insert(u3);

        var expected = List.of(u1, u2, u3);
        var actual = repoTest.getAll();

        // then
        assertThat(actual).isEqualTo(expected);
        assertThat(actual.get(0).getAddress()).isEqualTo(expected.get(0).getAddress());
        assertThat(actual.get(0).getPersonalInfo()).isEqualTo(expected.get(0).getPersonalInfo());
        assertThat(actual.get(1).getAddress()).isEqualTo(expected.get(1).getAddress());
        assertThat(actual.get(1).getPersonalInfo()).isEqualTo(expected.get(1).getPersonalInfo());
        assertThat(actual.get(2).getAddress()).isEqualTo(expected.get(2).getAddress());
        assertThat(actual.get(2).getPersonalInfo()).isEqualTo(expected.get(2).getPersonalInfo());
    }

    @Test
    void shouldFindUserByEmail() {
        // given
        var repoTest = new UserRepository(entityManagerFactory);

        var u1 = new User();
        var u2 = new User();
        var u3 = new User();

        var pi1 = new User.PersonalInfo();
        var pi2 = new User.PersonalInfo();
        var pi3 = new User.PersonalInfo();

        var a1 = new Address();
        var a2 = new Address();
        var a3 = new Address();

        pi1.setPhoneNumber("0994824689");
        pi1.setFirstname("Dmytro");
        pi1.setEmail("dmytro@ukr.net");

        u1.setPersonalInfo(pi1);
        u1.setAddress(a1);
        u1.setAccess(Access.ADMINISTRATOR);

        pi2.setPhoneNumber("0964269257");
        pi2.setEmail("1pan.maxim.52@gmail.com");
        pi2.setFirstname("Maksym");
        pi2.setLastname("Panov");

        a2.setRegion("Kharkiv");
        a2.setDistrict("Chuguiv");
        a2.setCity("Korobochkine");
        a2.setStreet("Shevchenka");
        a2.setBuilding(218);
        a2.setPostalCode(63540);

        u2.setPersonalInfo(pi2);
        u2.setAddress(a2);
        u2.setAccess(Access.USER);

        pi3.setPhoneNumber("0964269111");

        u3.setPersonalInfo(pi3);
        u3.setAddress(a3);
        u3.setAccess(Access.MANAGER);

        // when

        repoTest.insert(u1);
        repoTest.insert(u2);
        repoTest.insert(u3);

        var expected1 = List.of(u2);
        var actual1 = repoTest.getByColumn("1pan.maxim.52@gmail.com", true);

        var expected2 = List.of(u1);
        var actual2 = repoTest.getByColumn("dmytro@ukr.net", true);

        var expected3 = Collections.emptyList();
        var actual3 = repoTest.getByColumn("unknown_email@unknown.unknown", true);

        // then
        assertThat(actual1).isEqualTo(expected1);
        assertThat(actual1.get(0).getPersonalInfo()).isEqualTo(expected1.get(0).getPersonalInfo());
        assertThat(actual1.get(0).getAddress()).isEqualTo(expected1.get(0).getAddress());
        assertThat(actual1.get(0).getAccess()).isEqualTo(expected1.get(0).getAccess());
        assertThat(actual2).isEqualTo(expected2);
        assertThat(actual2.get(0).getPersonalInfo()).isEqualTo(expected2.get(0).getPersonalInfo());
        assertThat(actual2.get(0).getAddress()).isEqualTo(expected2.get(0).getAddress());
        assertThat(actual2.get(0).getAccess()).isEqualTo(expected2.get(0).getAccess());
        assertThat(actual3).isEqualTo(expected3);
    }

    @Test
    void shouldFindUserByPhoneNumber() {
        // given
        var repoTest = new UserRepository(entityManagerFactory);

        var u1 = new User();
        var u2 = new User();
        var u3 = new User();

        var pi1 = new User.PersonalInfo();
        var pi2 = new User.PersonalInfo();
        var pi3 = new User.PersonalInfo();

        var a1 = new Address();
        var a2 = new Address();
        var a3 = new Address();

        pi1.setPhoneNumber("0994824689");
        pi1.setFirstname("Dmytro");

        u1.setPersonalInfo(pi1);
        u1.setAddress(a1);
        u1.setAccess(Access.ADMINISTRATOR);

        pi2.setPhoneNumber("0964269257");
        pi2.setEmail("1pan.maxim.52@gmail.com");
        pi2.setFirstname("Maksym");
        pi2.setLastname("Panov");

        a2.setRegion("Kharkiv");
        a2.setDistrict("Chuguiv");
        a2.setCity("Korobochkine");
        a2.setStreet("Shevchenka");
        a2.setBuilding(218);
        a2.setPostalCode(63540);

        u2.setPersonalInfo(pi2);
        u2.setAddress(a2);
        u2.setAccess(Access.USER);

        pi3.setPhoneNumber("0964269111");

        u3.setPersonalInfo(pi3);
        u3.setAddress(a3);
        u3.setAccess(Access.MANAGER);

        // when

        repoTest.insert(u1);
        repoTest.insert(u2);
        repoTest.insert(u3);

        var expected1 = List.of(u1);
        var actual1 = repoTest.getByColumn("0994824689", true);

        var expected2 = List.of(u2);
        var actual2 = repoTest.getByColumn("0964269257", true);

        var expected3 = List.of(u3);
        var actual3 = repoTest.getByColumn("0964269111", true);

        var expected4 = Collections.emptyList();
        var actual4 = repoTest.getByColumn("0000000000", true);

        // then
        assertThat(actual1).isEqualTo(expected1);
        assertThat(actual1.get(0).getPersonalInfo()).isEqualTo(expected1.get(0).getPersonalInfo());
        assertThat(actual1.get(0).getAddress()).isEqualTo(expected1.get(0).getAddress());
        assertThat(actual1.get(0).getAccess()).isEqualTo(expected1.get(0).getAccess());
        assertThat(actual2).isEqualTo(expected2);
        assertThat(actual2.get(0).getPersonalInfo()).isEqualTo(expected2.get(0).getPersonalInfo());
        assertThat(actual2.get(0).getAddress()).isEqualTo(expected2.get(0).getAddress());
        assertThat(actual2.get(0).getAccess()).isEqualTo(expected2.get(0).getAccess());
        assertThat(actual3).isEqualTo(expected3);
        assertThat(actual3.get(0).getPersonalInfo()).isEqualTo(expected3.get(0).getPersonalInfo());
        assertThat(actual3.get(0).getAddress()).isEqualTo(expected3.get(0).getAddress());
        assertThat(actual3.get(0).getAccess()).isEqualTo(expected3.get(0).getAccess());
        assertThat(actual4).isEqualTo(expected4);
    }

    @Test
    void shouldUpdateFieldsInDatabase() {
        // given
        var repoTest = new UserRepository(entityManagerFactory);

        var u1 = new User();
        var u2 = new User();

        var pi1 = new User.PersonalInfo();
        var pi2 = new User.PersonalInfo();

        var a1 = new Address();
        var a2 = new Address();

        pi1.setPhoneNumber("0994824689");
        pi1.setFirstname("Dmytro");

        u1.setPersonalInfo(pi1);
        u1.setAddress(a1);
        u1.setAccess(Access.ADMINISTRATOR);

        pi2.setPhoneNumber("0964269257");
        pi2.setEmail("1pan.maxim.52@gmail.com");
        pi2.setFirstname("Maksym");
        pi2.setLastname("Panov");

        a2.setRegion("Kharkiv");
        a2.setDistrict("Chuguiv");
        a2.setCity("Korobochkine");
        a2.setStreet("Shevchenka");
        a2.setBuilding(218);
        a2.setPostalCode(63540);

        u2.setPersonalInfo(pi2);
        u2.setAddress(a2);
        u2.setAccess(Access.USER);

        // when

        var id1 = repoTest.insert(u1);
        var id2 = repoTest.insert(u2);

        u1.getPersonalInfo().setPhoneNumber("0000000000");
        u1.getPersonalInfo().setEmail("email1@gmail.com");
        u1.getPersonalInfo().setFirstname("firstname1");
        u1.getPersonalInfo().setLastname("lastname1");

        u1.getAddress().setRegion("region 1");
        u1.getAddress().setDistrict("district 1");
        u1.getAddress().setCity("city 1");
        u1.getAddress().setStreet("street 1");
        u1.getAddress().setBuilding(1);
        u1.getAddress().setApartment(1);
        u1.getAddress().setPostalCode(11111);

        u1.setAccess(Access.USER);

        u2.getPersonalInfo().setPhoneNumber("0111111111");
        u2.getPersonalInfo().setEmail(null);
        u2.getPersonalInfo().setFirstname("firstname2");
        u2.getPersonalInfo().setLastname(null);

        u2.setAddress(new Address());

        u2.setAccess(Access.ADMINISTRATOR);

        repoTest.update(u1);
        repoTest.update(u2);

        var upd1 = repoTest.get(id1);
        var upd2 = repoTest.get(id2);

        // then
        assertThat(upd1).isNotNull();
        assertThat(upd1.isPresent()).isTrue();
        assertThat(upd1.get()).isEqualTo(u1);
        assertThat(upd1.get().getPersonalInfo()).isEqualTo(u1.getPersonalInfo());
        assertThat(upd1.get().getAddress()).isEqualTo(u1.getAddress());
        assertThat(upd1.get().getAccess()).isEqualTo(u1.getAccess());

        assertThat(upd2).isNotNull();
        assertThat(upd2.isPresent()).isTrue();
        assertThat(upd2.get()).isEqualTo(u2);
        assertThat(upd2.get().getPersonalInfo()).isEqualTo(u2.getPersonalInfo());
        assertThat(upd2.get().getAddress()).isEqualTo(u2.getAddress());
        assertThat(upd2.get().getAccess()).isEqualTo(u2.getAccess());
    }

    @Test
    void shouldDeleteUsers() {
        // given
        var repoTest = new UserRepository(entityManagerFactory);

        var u1 = new User();
        var u2 = new User();
        var u3 = new User();

        var pi1 = new User.PersonalInfo();
        var pi2 = new User.PersonalInfo();
        var pi3 = new User.PersonalInfo();

        var a1 = new Address();
        var a2 = new Address();
        var a3 = new Address();

        pi1.setPhoneNumber("0994824689");
        pi1.setFirstname("Dmytro");

        u1.setPersonalInfo(pi1);
        u1.setAddress(a1);
        u1.setAccess(Access.ADMINISTRATOR);

        pi2.setPhoneNumber("0964269257");
        pi2.setEmail("1pan.maxim.52@gmail.com");
        pi2.setFirstname("Maksym");
        pi2.setLastname("Panov");

        a2.setRegion("Kharkiv");
        a2.setDistrict("Chuguiv");
        a2.setCity("Korobochkine");
        a2.setStreet("Shevchenka");
        a2.setBuilding(218);
        a2.setPostalCode(63540);

        u2.setPersonalInfo(pi2);
        u2.setAddress(a2);
        u2.setAccess(Access.USER);

        pi3.setPhoneNumber("0934622036");
        pi3.setFirstname("coucou");

        u3.setPersonalInfo(pi3);
        u3.setAddress(a3);
        u3.setAccess(Access.MANAGER);

        // when

        var id1 = repoTest.insert(u1);
        var id2 = repoTest.insert(u2);
        var id3 = repoTest.insert(u3);

        var beforeDeletionExpected = List.of(u1, u2, u3);
        var beforeDeletionActual = repoTest.getAll();

        repoTest.delete(id1);
        var afterFirstDeletionExpected = List.of(u2, u3);
        var afterFirstDeletionActual = repoTest.getAll();

        repoTest.delete(id3);
        var afterSecondDeletionExpected = List.of(u2);
        var afterSecondDeletionActual = repoTest.getAll();

        repoTest.delete(id2);
        var afterThirdDeletionExpected = Collections.emptyList();
        var afterThirdDeletionActual = repoTest.getAll();

        // then

        assertThat(beforeDeletionActual).isEqualTo(beforeDeletionExpected);
        assertThat(afterFirstDeletionActual).isEqualTo(afterFirstDeletionExpected);
        assertThat(afterSecondDeletionActual).isEqualTo(afterSecondDeletionExpected);
        assertThat(afterThirdDeletionActual).isEqualTo(afterThirdDeletionExpected);
    }
}
