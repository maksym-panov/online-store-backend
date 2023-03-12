package com.panov.store.dao;

import com.panov.store.Utils;
import com.panov.store.model.*;
import com.panov.store.utils.Access;
import com.panov.store.utils.Status;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.junit.Ignore;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.sql.Timestamp;

import static org.assertj.core.api.Assertions.assertThat;

public class OrderRepositoryTest {
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
    void shouldAddNewOrderAndOrderProductsToDatabaseAndReturnActualId() {
        // given
        var userRepository = new UserRepository(entityManagerFactory);
        var productTypeRepository = new ProductTypeRepository(entityManagerFactory);
        var productRepository = new ProductRepository(entityManagerFactory);
        var repoTest = new OrderRepository(entityManagerFactory);

        var productType1 = new ProductType();
        var productType2 = new ProductType();
        var productType3 = new ProductType();
        var productType4 = new ProductType();

        var dt = new DeliveryType();

        dt.setName("Nova Poshta");

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

        productTypeRepository.insert(productType1);
        productTypeRepository.insert(productType2);
        productTypeRepository.insert(productType3);
        productTypeRepository.insert(productType4);

        productRepository.insert(product1);
        productRepository.insert(product2);
        productRepository.insert(product3);

        var u = new User();

        var pi = new User.PersonalInfo();

        var a = new Address();

        pi.setPhoneNumber("0994824689");
        pi.setFirstname("Dmytro");

        u.setPersonalInfo(pi);
        u.setAddress(a);
        u.setAccess(Access.ADMINISTRATOR);

        userRepository.insert(u);

        ///////////

        var o = new Order();

        o.setDeliveryType(dt);
        dt.getOrders().add(o);
        o.setPostTime(new Timestamp(System.currentTimeMillis() - 1000000));
        o.setStatus(Status.POSTED);
        o.setUser(u);
        u.getOrders().add(o);

        var op1 = new OrderProducts();
        var op2 = new OrderProducts();
        var op3 = new OrderProducts();

        op1.setProduct(product1);
        op1.setOrder(o);
        op1.setQuantity(10);

        op2.setProduct(product2);
        op2.setOrder(o);
        op2.setQuantity(5);


        op3.setProduct(product3);
        op3.setOrder(o);
        op3.setQuantity(1);

        o.getOrderProducts().add(op1);
        o.getOrderProducts().add(op2);
        o.getOrderProducts().add(op3);


        // when

        var id = repoTest.insert(o);

        var opid1 = o.getOrderProducts().get(0).getOrderProductsId();
        var opid2 = o.getOrderProducts().get(1).getOrderProductsId();
        var opid3 = o.getOrderProducts().get(2).getOrderProductsId();

        var orderActual = repoTest.get(id);

        var em = entityManagerFactory.createEntityManager();

        em.getTransaction().begin();

        var op1Actual = em.find(OrderProducts.class, opid1);
        var op2Actual = em.find(OrderProducts.class, opid2);
        var op3Actual = em.find(OrderProducts.class, opid3);

        em.getTransaction().commit();
        em.close();

        // then
        assertThat(id).isNotNull();
        assertThat(orderActual).isNotNull();
        assertThat(orderActual.isPresent()).isTrue();
        assertThat(orderActual.get().getUser()).isEqualTo(o.getUser());
        assertThat(orderActual.get().getUnregisteredCustomer()).isEqualTo(o.getUnregisteredCustomer());
        assertThat(orderActual.get().getOrderProducts()).isEqualTo(o.getOrderProducts());
        assertThat(orderActual.get().getDeliveryType()).isEqualTo(o.getDeliveryType());
        assertThat(orderActual.get().getPostTime()).isEqualTo(o.getPostTime());
        assertThat(orderActual.get().getCompleteTime()).isEqualTo(o.getCompleteTime());
        assertThat(orderActual.get().getStatus()).isEqualTo(o.getStatus());

        assertThat(opid1).isNotNull();
        assertThat(op1Actual).isEqualTo(op1);
        assertThat(opid2).isNotNull();
        assertThat(op2Actual).isEqualTo(op2);
        assertThat(opid3).isNotNull();
        assertThat(op3Actual).isEqualTo(op3);
    }

    @Test
    @Ignore
    void shouldReturnAllAddedOrders() {

    }

    @Test
    @Ignore
    void shouldThrowIfUsingFindByColumn() {

    }

    @Test
    @Ignore
    void shouldUpdateOrderAndAddOrDeleteOrderProductsInDatabase() {

    }

    @Test
    @Ignore
    void shouldDeleteOrdersAndOrderProducts() {

    }
}
