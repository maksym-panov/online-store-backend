package com.panov.store.dao;

import com.panov.store.Utils;
import com.panov.store.model.ProductType;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class ProductTypeRepositoryTest {
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
    void shouldInsertAndFindExactlyTheseProductTypes() {
        // given
        var repositoryUnderTest = new ProductTypeRepository(entityManagerFactory);

        var type1 = new ProductType();
        var type2 = new ProductType();
        var type3 = new ProductType();

        type1.setName("Bread");
        type2.setName("Meat");
        type3.setName("Water");

        // when

        repositoryUnderTest.insert(type1);
        repositoryUnderTest.insert(type2);
        repositoryUnderTest.insert(type3);

        var expected = List.of(type1, type2, type3);
        var actual = repositoryUnderTest.getAll();

        // then
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void productTypesThatWeGetShouldNotEqualToDifferentOnes() {
        // given
        var repositoryUnderTest = new ProductTypeRepository(entityManagerFactory);

        var type1 = new ProductType();
        var type2 = new ProductType();
        var type3 = new ProductType();

        type1.setName("Bread");
        type2.setName("Meat");
        type3.setName("Water");

        // when

        repositoryUnderTest.insert(type1);
        repositoryUnderTest.insert(type2);
        repositoryUnderTest.insert(type3);

        var typeUnexpected1 = new ProductType();
        var typeUnexpected2 = new ProductType();
        var typeUnexpected3 = new ProductType();

        typeUnexpected1.setName("Fruit");
        typeUnexpected2.setName("Toy");
        typeUnexpected3.setName("Zoom");

        var unexpected = List.of(typeUnexpected1, typeUnexpected2, typeUnexpected3);
        var expected = List.of(type1, type2, type3);
        var actual = repositoryUnderTest.getAll();

        // then

        assertThat(actual).isEqualTo(expected);
        assertThat(actual).isNotEqualTo(unexpected);
    }

    @Test
    void shouldReturnIdOfAddedEntityAndReturnTheSameEntityByThisId() {
        // given
        var repositoryUnderTest = new ProductTypeRepository(entityManagerFactory);

        var type1 = new ProductType();
        var type2 = new ProductType();
        var type3 = new ProductType();

        type1.setName("Bread");
        type2.setName("Meat");
        type3.setName("Water");

        // when
        var id1 = repositoryUnderTest.insert(type1);
        var id2 = repositoryUnderTest.insert(type2);
        var id3 = repositoryUnderTest.insert(type3);

        // then
        assertThat(id1).isNotNull();
        var actualType1 = repositoryUnderTest.get(id1);
        assertThat(actualType1).isNotNull();
        assertThat(actualType1.isPresent()).isTrue();
        assertThat(actualType1.get().getProductTypeId()).isEqualTo(id1);
        assertThat(actualType1.get()).isEqualTo(type1);

        assertThat(id2).isNotNull();
        var actualType2 = repositoryUnderTest.get(id2);
        assertThat(actualType2).isNotNull();
        assertThat(actualType2.isPresent()).isTrue();
        assertThat(actualType2.get().getProductTypeId()).isEqualTo(id2);
        assertThat(actualType2.get()).isEqualTo(type2);

        assertThat(id3).isNotNull();
        var actualType3 = repositoryUnderTest.get(id3);
        assertThat(actualType3).isNotNull();
        assertThat(actualType3.isPresent()).isTrue();
        assertThat(actualType3.get().getProductTypeId()).isEqualTo(id3);
        assertThat(actualType3.get()).isEqualTo(type3);
    }

    @Test
    void shouldReturnProductTypeListByPartOfItsNameIgnoringCase() {
        // given
        var repositoryUnderTest = new ProductTypeRepository(entityManagerFactory);

        var type1 = new ProductType();
        var type2 = new ProductType();
        var type3 = new ProductType();

        type1.setName("Bread");
        type2.setName("Meat");
        type3.setName("Water");

        // when

        repositoryUnderTest.insert(type1);
        repositoryUnderTest.insert(type2);
        repositoryUnderTest.insert(type3);

        var expectedByLetter_E = List.of(type1, type2, type3);
        var actualByLetter_E = repositoryUnderTest.getByColumn("E", false);

        var expectedByLetter_e = List.of(type1, type2, type3);
        var actualByLetter_e = repositoryUnderTest.getByColumn("e", false);

        var expectedBy_EA = List.of(type1, type2);
        var actualBy_EA = repositoryUnderTest.getByColumn("EA", false);

        var expectedBy_ea = List.of(type1, type2);
        var actualBy_ea = repositoryUnderTest.getByColumn("ea", false);

        var expectedBy_aldsfjkj = Collections.emptyList();
        var actualBy_aldsfjkj = repositoryUnderTest.getByColumn("aldsfjkj", false);

        var expectedBy_BREAD = List.of(type1);
        var actualBy_BREAD = repositoryUnderTest.getByColumn("BREAD", false);

        // then
        assertThat(actualByLetter_E).isEqualTo(expectedByLetter_E);
        assertThat(actualByLetter_e).isEqualTo(expectedByLetter_e);
        assertThat(actualBy_EA).isEqualTo(expectedBy_EA);
        assertThat(actualBy_ea).isEqualTo(expectedBy_ea);
        assertThat(actualBy_aldsfjkj).isEqualTo(expectedBy_aldsfjkj);
        assertThat(actualBy_BREAD).isEqualTo(expectedBy_BREAD);
    }

    @Test
    void shouldReturnActualIdAfterUpdate() {
        // given
        var repositoryUnderTest = new ProductTypeRepository(entityManagerFactory);

        var type1 = new ProductType();
        var type2 = new ProductType();
        var type3 = new ProductType();

        type1.setName("Bread");
        type2.setName("Meat");
        type3.setName("Water");

        var expectedId1 = repositoryUnderTest.insert(type1);
        var expectedId2 = repositoryUnderTest.insert(type2);
        var expectedId3 = repositoryUnderTest.insert(type3);

        // when

        type1.setName("1111");
        type2.setName("2222");
        type3.setName("3333");

        var actualId1 = repositoryUnderTest.update(type1);
        var actualId2 = repositoryUnderTest.update(type2);
        var actualId3 = repositoryUnderTest.update(type3);

        // then

        assertThat(actualId1).isEqualTo(expectedId1);
        assertThat(actualId2).isEqualTo(expectedId2);
        assertThat(actualId3).isEqualTo(expectedId3);
    }

    @Test
    void shouldChangeFieldsInDatabase() {
        // given
        var repositoryUnderTest = new ProductTypeRepository(entityManagerFactory);

        var type1 = new ProductType();
        var type2 = new ProductType();
        var type3 = new ProductType();

        type1.setName("Bread");
        type2.setName("Meat");
        type3.setName("Water");

        var id1 = repositoryUnderTest.insert(type1);
        var id2 = repositoryUnderTest.insert(type2);
        var id3 = repositoryUnderTest.insert(type3);

        // when

        type1.setName("1111");
        type2.setName("2222");
        type3.setName("3333");

        repositoryUnderTest.update(type1);
        repositoryUnderTest.update(type2);
        repositoryUnderTest.update(type3);

        var afterChangeFromDB1 = repositoryUnderTest.get(id1);
        var afterChangeFromDB2 = repositoryUnderTest.get(id2);
        var afterChangeFromDB3 = repositoryUnderTest.get(id3);

        // then

        assertThat(afterChangeFromDB1).isNotNull();
        assertThat(afterChangeFromDB1.isPresent()).isTrue();
        assertThat(afterChangeFromDB1.get()).isEqualTo(type1);

        assertThat(afterChangeFromDB2).isNotNull();
        assertThat(afterChangeFromDB2.isPresent()).isTrue();
        assertThat(afterChangeFromDB2.get()).isEqualTo(type2);

        assertThat(afterChangeFromDB3).isNotNull();
        assertThat(afterChangeFromDB3.isPresent()).isTrue();
        assertThat(afterChangeFromDB3.get()).isEqualTo(type3);
    }

    @Test
    void shouldDeleteProductTypes() {
        // given
        var repositoryUnderTest = new ProductTypeRepository(entityManagerFactory);

        var type1 = new ProductType();
        var type2 = new ProductType();
        var type3 = new ProductType();

        type1.setName("Bread");
        type2.setName("Meat");
        type3.setName("Water");

        var id1 = repositoryUnderTest.insert(type1);
        var id2 = repositoryUnderTest.insert(type2);
        var id3 = repositoryUnderTest.insert(type3);

        // when

        repositoryUnderTest.delete(id1);
        var expectedAfterFirstDeletion = List.of(type2, type3);
        var actualAfterFirstDeletion = repositoryUnderTest.getAll();

        repositoryUnderTest.delete(id3);
        var expectedAfterSecondDeletion = List.of(type2);
        var actualAfterSecondDeletion = repositoryUnderTest.getAll();

        repositoryUnderTest.delete(id2);
        var expectedAfterThirdDeletion = Collections.emptyList();
        var actualAfterThirdDeletion = repositoryUnderTest.getAll();

        // then

        assertThat(actualAfterFirstDeletion).isEqualTo(expectedAfterFirstDeletion);
        assertThat(actualAfterSecondDeletion).isEqualTo(expectedAfterSecondDeletion);
        assertThat(actualAfterThirdDeletion).isEqualTo(expectedAfterThirdDeletion);
    }
}
