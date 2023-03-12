package com.panov.store.dao;

import com.panov.store.Utils;
import com.panov.store.model.Product;
import com.panov.store.model.ProductType;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class ProductRepositoryTest {
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
    void shouldInsertAndFindExactlyTheseProducts() {
        // given
        var productTypeRepository = new ProductTypeRepository(entityManagerFactory);
        var repositoryUnderTest = new ProductRepository(entityManagerFactory);

        var productType1 = new ProductType();
        var productType2 = new ProductType();
        var productType3 = new ProductType();
        var productType4 = new ProductType();

        productType1.setName("Grocery");
        productType2.setName("Bread");
        productType3.setName("Meat");
        productType4.setName("Toy");

        var product1 = new Product();
        var product2 = new Product();
        var product3 = new Product();

        product1.setName("Bread bar");
        product1.setDescription("Just a bread");
        product1.setPrice(new BigDecimal("10.20"));
        product1.setStock(10);
        product1.getProductTypes().add(productType1);
        product1.getProductTypes().add(productType2);

        product2.setName("Steak");
        product2.setDescription("Beautiful rib roast");
        product2.setPrice(new BigDecimal("550.00"));
        product2.setStock(5);
        product2.getProductTypes().add(productType1);
        product2.getProductTypes().add(productType3);

        product3.setName("Hot Wheels!");
        product3.setDescription("Make your little boy happy with this new Hot Wheels kit");
        product3.setPrice(new BigDecimal("1500.00"));
        product3.setStock(20);
        product3.getProductTypes().add(productType4);

        // when

        productTypeRepository.insert(productType1);
        productTypeRepository.insert(productType2);
        productTypeRepository.insert(productType3);
        productTypeRepository.insert(productType4);

        repositoryUnderTest.insert(product1);
        repositoryUnderTest.insert(product2);
        repositoryUnderTest.insert(product3);

        var expected = List.of(product1, product2, product3);
        var actual = repositoryUnderTest.getAll();

        // then

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void shouldReturnIdOfAddedEntityAndReturnTheSameEntityByThisId() {
        // given
        var productTypeRepository = new ProductTypeRepository(entityManagerFactory);
        var repositoryUnderTest = new ProductRepository(entityManagerFactory);

        var productType1 = new ProductType();
        var productType2 = new ProductType();
        var productType3 = new ProductType();
        var productType4 = new ProductType();

        productType1.setName("Grocery");
        productType2.setName("Bread");
        productType3.setName("Meat");
        productType4.setName("Toy");

        var product1 = new Product();
        var product2 = new Product();
        var product3 = new Product();

        product1.setName("Bread bar");
        product1.setDescription("Just a bread");
        product1.setPrice(new BigDecimal("10.20"));
        product1.setStock(10);
        product1.getProductTypes().add(productType1);
        product1.getProductTypes().add(productType2);

        product2.setName("Steak");
        product2.setDescription("Beautiful rib roast");
        product2.setPrice(new BigDecimal("550.00"));
        product2.setStock(5);
        product2.getProductTypes().add(productType1);
        product2.getProductTypes().add(productType3);

        product3.setName("Hot Wheels!");
        product3.setDescription("Make your little boy happy with this new Hot Wheels kit");
        product3.setPrice(new BigDecimal("1500.00"));
        product3.setStock(20);
        product3.getProductTypes().add(productType4);

        // when

        productTypeRepository.insert(productType1);
        productTypeRepository.insert(productType2);
        productTypeRepository.insert(productType3);
        productTypeRepository.insert(productType4);

        var id1 = repositoryUnderTest.insert(product1);
        var id2 = repositoryUnderTest.insert(product2);
        var id3 = repositoryUnderTest.insert(product3);

        // then

        assertThat(id1).isNotNull();
        var actual1 = repositoryUnderTest.get(id1);
        assertThat(actual1).isNotNull();
        assertThat(actual1.isPresent()).isTrue();
        assertThat(actual1.get()).isEqualTo(product1);
        assertThat(actual1.get().getProductTypes()).isEqualTo(product1.getProductTypes());

        assertThat(id2).isNotNull();
        var actual2 = repositoryUnderTest.get(id2);
        assertThat(actual2).isNotNull();
        assertThat(actual2.isPresent()).isTrue();
        assertThat(actual2.get()).isEqualTo(product2);
        assertThat(actual2.get().getProductTypes()).isEqualTo(product2.getProductTypes());

        assertThat(id3).isNotNull();
        var actual3 = repositoryUnderTest.get(id3);
        assertThat(actual3).isNotNull();
        assertThat(actual3.isPresent()).isTrue();
        assertThat(actual3.get()).isEqualTo(product3);
        assertThat(actual3.get().getProductTypes()).isEqualTo(product3.getProductTypes());
    }

    @Test
    void shouldNotAddNewProductTypesIfTheyDontExist() {
        // given
        var productTypeRepository = new ProductTypeRepository(entityManagerFactory);
        var repositoryUnderTest = new ProductRepository(entityManagerFactory);

        var productType1 = new ProductType();
        var productType2 = new ProductType();
        var productType3 = new ProductType();

        productType1.setName("Grocery");
        productType2.setName("Bread");

        productType3.setProductTypeId(10000000);
        productType3.setName("Some type");

        var product = new Product();

        product.setName("Bread bar");
        product.setDescription("Just a bread");
        product.setPrice(new BigDecimal("10.20"));
        product.setStock(10);
        product.getProductTypes().add(productType1);
        product.getProductTypes().add(productType2);
        product.getProductTypes().add(productType3);

        // when

        productTypeRepository.insert(productType2);

        var id = repositoryUnderTest.insert(product);

        var actual = repositoryUnderTest.get(id);
        var notExpected = Set.of(productType1, productType2, productType3);

        // then

        assertThat(id).isNotNull();
        assertThat(actual).isNotNull();
        assertThat(actual.isPresent()).isTrue();

        assertThat(actual.get().getProductTypes()).isNotEqualTo(notExpected);
    }

    @Test
    void productsThatWeGetShouldNotBeEqualToDifferentOnes() {
        // given
        var productTypeRepository = new ProductTypeRepository(entityManagerFactory);
        var repositoryUnderTest = new ProductRepository(entityManagerFactory);

        var productType1 = new ProductType();
        var productType2 = new ProductType();
        var productType3 = new ProductType();
        var productType4 = new ProductType();

        productType1.setName("Grocery");
        productType2.setName("Bread");
        productType3.setName("Meat");
        productType4.setName("Toy");

        var product1 = new Product();
        var product2 = new Product();
        var product3 = new Product();

        var product4444 = new Product();
        var product5555 = new Product();

        product1.setName("Bread bar");
        product1.setDescription("Just a bread");
        product1.setPrice(new BigDecimal("10.20"));
        product1.setStock(10);
        product1.getProductTypes().add(productType1);
        product1.getProductTypes().add(productType2);

        product2.setName("Steak");
        product2.setDescription("Beautiful rib roast");
        product2.setPrice(new BigDecimal("550.00"));
        product2.setStock(5);
        product2.getProductTypes().add(productType1);
        product2.getProductTypes().add(productType3);

        product3.setName("Hot Wheels!");
        product3.setDescription("Make your little boy happy with this new Hot Wheels kit");
        product3.setPrice(new BigDecimal("1500.00"));
        product3.setStock(20);
        product3.getProductTypes().add(productType4);

        product4444.setName("Booooo");
        product4444.setPrice(new BigDecimal("666.66"));
        product4444.setStock(666);

        product5555.setName("Coucou");
        product5555.setPrice(new BigDecimal("50"));
        product5555.setStock(10);

        // when

        productTypeRepository.insert(productType1);
        productTypeRepository.insert(productType2);
        productTypeRepository.insert(productType3);
        productTypeRepository.insert(productType4);

        var id1 = repositoryUnderTest.insert(product1);
        var id2 = repositoryUnderTest.insert(product2);
        var id3 = repositoryUnderTest.insert(product3);

        // then

        var first = repositoryUnderTest.get(id1);
        var second = repositoryUnderTest.get(id2);
        var third = repositoryUnderTest.get(id3);

        assertThat(first).isNotNull();
        assertThat(first.isPresent()).isTrue();
        assertThat(first.get()).isNotEqualTo(product4444);
        assertThat(first.get()).isNotEqualTo(product5555);

        assertThat(second).isNotNull();
        assertThat(second.isPresent()).isTrue();
        assertThat(second.get()).isNotEqualTo(product4444);
        assertThat(second.get()).isNotEqualTo(product5555);

        assertThat(third).isNotNull();
        assertThat(third.isPresent()).isTrue();
        assertThat(third.get()).isNotEqualTo(product4444);
        assertThat(third.get()).isNotEqualTo(product5555);
    }

    @Test
    void shouldReturnEmptyIfIdDoesNotExists() {
        // given
        var repositoryUnderTest = new ProductRepository(entityManagerFactory);

        var product = new Product();

        product.setName("Bread bar");
        product.setDescription("....");
        product.setPrice(new BigDecimal("10.20"));
        product.setStock(10);

        // when

        var id = repositoryUnderTest.insert(product);

        var notExistingId = id + 100000;

        var result = repositoryUnderTest.get(notExistingId);

        // then
        assertThat(result.isPresent()).isFalse();
    }

    @Test
    void shouldReturnProductListByPartOfItsNameIgnoringCase() {
        // given
        var repositoryUnderTest = new ProductRepository(entityManagerFactory);

        var product1 = new Product();
        var product2 = new Product();
        var product3 = new Product();
        var product4 = new Product();
        var product5 = new Product();

        product1.setName("Bread bar");
        product1.setDescription("Just a bread");
        product1.setPrice(new BigDecimal("10.20"));
        product1.setStock(10);

        product2.setName("Steak");
        product2.setDescription("Beautiful rib roast");
        product2.setPrice(new BigDecimal("550.00"));
        product2.setStock(5);

        product3.setName("Hot Wheels!");
        product3.setDescription("Make your little boy happy with this new Hot Wheels kit");
        product3.setPrice(new BigDecimal("1500.00"));
        product3.setStock(20);

        product4.setName("Booooo");
        product4.setPrice(new BigDecimal("666.66"));
        product4.setStock(666);

        product5.setName("Coucou");
        product5.setPrice(new BigDecimal("50"));
        product5.setStock(10);

        // when
        repositoryUnderTest.insert(product1);
        repositoryUnderTest.insert(product2);
        repositoryUnderTest.insert(product3);
        repositoryUnderTest.insert(product4);
        repositoryUnderTest.insert(product5);

        var expectedBy_o = List.of(product3, product4, product5);
        var actualBy_o = repositoryUnderTest.getByColumn("o");

        var expectedBy_O = List.of(product3, product4, product5);
        var actualBy_O = repositoryUnderTest.getByColumn("O");

        var expectedBy_aaaaaaaaa = Collections.emptyList();
        var actualBy_aaaaaaaaa = repositoryUnderTest.getByColumn("aaaaaaaaa");

        var expectedBy_Ea = List.of(product1, product2);
        var actualBy_Ea = repositoryUnderTest.getByColumn("Ea");

        var actualBy_null = repositoryUnderTest.getByColumn(null);

        // then
        assertThat(actualBy_o).isEqualTo(expectedBy_o);
        assertThat(actualBy_O).isEqualTo(expectedBy_O);
        assertThat(actualBy_aaaaaaaaa).isEqualTo(expectedBy_aaaaaaaaa);
        assertThat(actualBy_Ea).isEqualTo(expectedBy_Ea);
        assertThat(actualBy_null).isNull();
    }

    @Test
    void shouldChangeFieldsInDatabase() {
        // given
        var productTypeRepository = new ProductTypeRepository(entityManagerFactory);
        var repositoryUnderTest = new ProductRepository(entityManagerFactory);

        var productType1 = new ProductType();
        var productType2 = new ProductType();
        var productType3 = new ProductType();
        var productType4 = new ProductType();

        productType1.setName("Grocery");
        productType2.setName("Bread");
        productType3.setName("Meat");
        productType4.setName("Toy");

        var product1 = new Product();
        var product2 = new Product();
        var product3 = new Product();

        product1.setName("Bread bar");
        product1.setDescription("Just a bread");
        product1.setPrice(new BigDecimal("10.20"));
        product1.setStock(10);
        product1.getProductTypes().add(productType1);
        product1.getProductTypes().add(productType2);

        product2.setName("Steak");
        product2.setDescription("Beautiful rib roast");
        product2.setPrice(new BigDecimal("550.00"));
        product2.setStock(5);
        product2.getProductTypes().add(productType1);
        product2.getProductTypes().add(productType3);

        product3.setName("Hot Wheels!");
        product3.setDescription("Make your little boy happy with this new Hot Wheels kit");
        product3.setPrice(new BigDecimal("1500.00"));
        product3.setStock(20);
        product3.getProductTypes().add(productType4);

        // when

        productTypeRepository.insert(productType1);
        productTypeRepository.insert(productType2);
        productTypeRepository.insert(productType3);
        productTypeRepository.insert(productType4);

        repositoryUnderTest.insert(product1);
        repositoryUnderTest.insert(product2);
        repositoryUnderTest.insert(product3);

        var newType = new ProductType();
        newType.setName("Common");

        productTypeRepository.insert(newType);

        product1.getProductTypes().add(newType);
        product1.getProductTypes().remove(productType1);
        product1.setName("New name 1");
        product1.setDescription("New description 1");
        product1.setPrice(new BigDecimal("1.00"));
        product1.setStock(1);

        product2.getProductTypes().add(newType);
        product2.getProductTypes().remove(productType1);
        product2.getProductTypes().remove(productType3);
        product2.setName("New name 2");
        product2.setDescription("New description 2");
        product2.setPrice(new BigDecimal("2.00"));
        product2.setStock(2);

        product3.setProductTypes(null);
        product3.setName("New name 3");
        product3.setDescription("New description 3");
        product3.setPrice(new BigDecimal("3.00"));
        product3.setStock(3);

        var id1 = repositoryUnderTest.update(product1);
        var id2 = repositoryUnderTest.update(product2);
        var id3 = repositoryUnderTest.update(product3);

        var update1 = repositoryUnderTest.get(id1);
        var update2 = repositoryUnderTest.get(id2);
        var update3 = repositoryUnderTest.get(id3);

        // then

        assertThat(id1).isNotNull();
        assertThat(id1).isEqualTo(product1.getProductId());
        assertThat(update1).isNotNull();
        assertThat(update1.isPresent()).isTrue();
        assertThat(update1.get()).isEqualTo(product1);
        assertThat(update1.get().getProductTypes()).isEqualTo(product1.getProductTypes());

        assertThat(id2).isNotNull();
        assertThat(id2).isEqualTo(product2.getProductId());
        assertThat(update2).isNotNull();
        assertThat(update2.isPresent()).isTrue();
        assertThat(update2.get()).isEqualTo(product2);
        assertThat(update2.get().getProductTypes()).isEqualTo(product2.getProductTypes());

        assertThat(id3).isNotNull();
        assertThat(id3).isEqualTo(product3.getProductId());
        assertThat(update3).isNotNull();
        assertThat(update3.isPresent()).isTrue();
        assertThat(update3.get()).isEqualTo(product3);
        assertThat(update3.get().getProductTypes()).isEqualTo(product3.getProductTypes());
    }

    @Test
    void shouldDeleteProductsAndShouldNotDeleteProductTypes() {
        // given
        var productTypeRepository = new ProductTypeRepository(entityManagerFactory);
        var repositoryUnderTest = new ProductRepository(entityManagerFactory);

        var productType1 = new ProductType();
        var productType2 = new ProductType();
        var productType3 = new ProductType();
        var productType4 = new ProductType();

        productType1.setName("Grocery");
        productType2.setName("Bread");
        productType3.setName("Meat");
        productType4.setName("Toy");

        var product1 = new Product();
        var product2 = new Product();
        var product3 = new Product();

        product1.setName("Bread bar");
        product1.setDescription("Just a bread");
        product1.setPrice(new BigDecimal("10.20"));
        product1.setStock(10);
        product1.getProductTypes().add(productType1);
        product1.getProductTypes().add(productType2);

        product2.setName("Steak");
        product2.setDescription("Beautiful rib roast");
        product2.setPrice(new BigDecimal("550.00"));
        product2.setStock(5);
        product2.getProductTypes().add(productType1);
        product2.getProductTypes().add(productType3);

        product3.setName("Hot Wheels!");
        product3.setDescription("Make your little boy happy with this new Hot Wheels kit");
        product3.setPrice(new BigDecimal("1500.00"));
        product3.setStock(20);
        product3.getProductTypes().add(productType4);

        // when

        productTypeRepository.insert(productType1);
        productTypeRepository.insert(productType2);
        productTypeRepository.insert(productType3);
        productTypeRepository.insert(productType4);

        repositoryUnderTest.insert(product1);
        repositoryUnderTest.insert(product2);
        repositoryUnderTest.insert(product3);

        var productTypesExpectedInvariant = productTypeRepository.getAll();

        var beforeDeletingExpected = List.of(product1, product2, product3);
        var beforeDeletingActual = repositoryUnderTest.getAll();

        repositoryUnderTest.delete(product1.getProductId());
        var afterFirstDeletionExpected = List.of(product2, product3);
        var afterFirstDeletionActual = repositoryUnderTest.getAll();
        var productTypesAfterFirstDeletion = productTypeRepository.getAll();

        repositoryUnderTest.delete(product3.getProductId());
        var afterSecondDeletionExpected = List.of(product2);
        var afterSecondDeletionActual = repositoryUnderTest.getAll();
        var productTypesAfterSecondDeletion = productTypeRepository.getAll();

        repositoryUnderTest.delete(product2.getProductId());
        var afterThirdDeletionExpected = Collections.emptyList();
        var afterThirdDeletionActual = repositoryUnderTest.getAll();
        var productTypesAfterThirdDeletion = productTypeRepository.getAll();

        // then

        assertThat(beforeDeletingActual).isEqualTo(beforeDeletingExpected);

        assertThat(afterFirstDeletionActual).isEqualTo(afterFirstDeletionExpected);
        assertThat(productTypesAfterFirstDeletion).isEqualTo(productTypesExpectedInvariant);

        assertThat(afterSecondDeletionActual).isEqualTo(afterSecondDeletionExpected);
        assertThat(productTypesAfterSecondDeletion).isEqualTo(productTypesExpectedInvariant);

        assertThat(afterThirdDeletionActual).isEqualTo(afterThirdDeletionExpected);
        assertThat(productTypesAfterThirdDeletion).isEqualTo(productTypesExpectedInvariant);
    }
}
