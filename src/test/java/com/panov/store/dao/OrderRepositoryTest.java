package com.panov.store.dao;

import com.panov.store.Utils;
import com.panov.store.model.*;
import com.panov.store.utils.Access;
import com.panov.store.utils.Status;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;

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
        var deliveryTypeRepository = new DeliveryTypeRepository(entityManagerFactory);

        var dt = new DeliveryType();

        var productType1 = new ProductType();
        var productType2 = new ProductType();
        var productType3 = new ProductType();
        var productType4 = new ProductType();

        var product1 = new Product();
        var product2 = new Product();
        var product3 = new Product();

        var u = new User();
        var pi = new User.PersonalInfo();
        var a = new Address();

        var o = new Order();

        var op1 = new OrderProducts();
        var op2 = new OrderProducts();
        var op3 = new OrderProducts();

        dt.setName("Nova Poshta");
        deliveryTypeRepository.insert(dt);

        productType1.setName("Grocery");
        productType2.setName("Bread");
        productType3.setName("Meat");
        productType4.setName("Toy");

        productTypeRepository.insert(productType1);
        productTypeRepository.insert(productType2);
        productTypeRepository.insert(productType3);
        productTypeRepository.insert(productType4);

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

        productRepository.insert(product1);
        productRepository.insert(product2);
        productRepository.insert(product3);

        pi.setPhoneNumber("0994824689");
        pi.setFirstname("Dmytro");
        u.setPersonalInfo(pi);
        u.setAddress(a);
        u.setAccess(Access.ADMINISTRATOR);
        userRepository.insert(u);

        ///////////

        o.setUser(u);
        o.setDeliveryType(dt);
        o.setPostTime(new Timestamp(System.currentTimeMillis() - 1000000));
        o.setStatus(Status.POSTED);

        op1.setProduct(product1);
        op1.setQuantity(10);
        op1.setOrder(o);

        op2.setProduct(product2);
        op2.setQuantity(5);
        op2.setOrder(o);


        op3.setProduct(product3);
        op3.setQuantity(1);
        op3.setOrder(o);

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
        assertThat(orderActual.get().getOrderProducts().size()).isEqualTo(o.getOrderProducts().size());

        assertThat(orderActual.get().getOrderProducts().get(0)).isEqualTo(o.getOrderProducts().get(0));
        assertThat(orderActual.get().getOrderProducts().get(1)).isEqualTo(o.getOrderProducts().get(1));
        assertThat(orderActual.get().getOrderProducts().get(2)).isEqualTo(o.getOrderProducts().get(2));
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
    void shouldReturnAllAddedOrders() {
        var userRepository = new UserRepository(entityManagerFactory);
        var productTypeRepository = new ProductTypeRepository(entityManagerFactory);
        var productRepository = new ProductRepository(entityManagerFactory);
        var repoTest = new OrderRepository(entityManagerFactory);
        var deliveryTypeRepository = new DeliveryTypeRepository(entityManagerFactory);
        var unregisteredCustomerRepository = new UnregisteredCustomerRepository(entityManagerFactory);

        var dt = new DeliveryType();

        var productType1 = new ProductType();
        var productType2 = new ProductType();
        var productType3 = new ProductType();
        var productType4 = new ProductType();

        var product1 = new Product();
        var product2 = new Product();
        var product3 = new Product();

        var u = new User();
        var pi = new User.PersonalInfo();
        var a1 = new Address();

        var uc = new UnregisteredCustomer();
        var a2 = new Address();

        var o1 = new Order();
        var o2 = new Order();

        var op1 = new OrderProducts();
        var op2 = new OrderProducts();
        var op3 = new OrderProducts();

        dt.setName("Nova Poshta");
        deliveryTypeRepository.insert(dt);

        productType1.setName("Grocery");
        productType2.setName("Bread");
        productType3.setName("Meat");
        productType4.setName("Toy");

        productTypeRepository.insert(productType1);
        productTypeRepository.insert(productType2);
        productTypeRepository.insert(productType3);
        productTypeRepository.insert(productType4);

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

        productRepository.insert(product1);
        productRepository.insert(product2);
        productRepository.insert(product3);

        pi.setPhoneNumber("0994824689");
        pi.setFirstname("Dmytro");
        u.setPersonalInfo(pi);
        u.setAddress(a1);
        u.setAccess(Access.ADMINISTRATOR);
        userRepository.insert(u);

        uc.setPhoneNumber("0964267234");
        uc.setFirstname("Maksym");
        a2.setRegion("reg");
        a2.setDistrict("dist");
        a2.setCity("city");
        a2.setStreet("st");
        a2.setBuilding(1);
        a2.setPostalCode(54123);
        uc.setAddress(a2);
        unregisteredCustomerRepository.insert(uc);

        ///////////

        o1.setUser(u);
        o1.setDeliveryType(dt);
        o1.setPostTime(new Timestamp(System.currentTimeMillis() - 1000000));
        o1.setStatus(Status.POSTED);

        op1.setProduct(product1);
        op1.setQuantity(10);
        op1.setOrder(o1);

        op2.setProduct(product2);
        op2.setQuantity(5);
        op2.setOrder(o1);


        op3.setProduct(product3);
        op3.setQuantity(1);
        op3.setOrder(o1);

        o1.getOrderProducts().add(op1);
        o1.getOrderProducts().add(op2);
        o1.getOrderProducts().add(op3);

        o2.setUnregisteredCustomer(uc);
        o2.setDeliveryType(dt);
        o2.setPostTime(new Timestamp(System.currentTimeMillis() - 1000000));
        o2.setStatus(Status.POSTED);


        // when

        repoTest.insert(o1);
        repoTest.insert(o2);

        var actual1 = repoTest.getAll().get(0);
        var actual2 = repoTest.getAll().get(1);

        assertThat(repoTest.getAll().size()).isEqualTo(2);

        var opActual1 = actual1.getOrderProducts();
        assertThat(opActual1.size()).isEqualTo(o1.getOrderProducts().size());
        assertThat(opActual1.get(0)).isEqualTo(op1);
        assertThat(opActual1.get(1)).isEqualTo(op2);
        assertThat(opActual1.get(2)).isEqualTo(op3);

        var opActual2 = actual2.getOrderProducts();
        assertThat(opActual2.isEmpty()).isEqualTo(o2.getOrderProducts().isEmpty());
    }

    @Test
    void shouldThrowIfUsingFindByColumn() {
        // given
        var repoTest = new OrderRepository(entityManagerFactory);

        // then
        assertThatExceptionOfType(UnsupportedOperationException.class)
                .isThrownBy(() -> repoTest.getByColumn(new Object(), false));
    }

    @Test
    void shouldUpdateOrderAndAddOrDeleteOrderProductsInDatabase() {
        // given
        var userRepository = new UserRepository(entityManagerFactory);
        var productTypeRepository = new ProductTypeRepository(entityManagerFactory);
        var productRepository = new ProductRepository(entityManagerFactory);
        var repoTest = new OrderRepository(entityManagerFactory);
        var deliveryTypeRepository = new DeliveryTypeRepository(entityManagerFactory);
        var unregisteredCustomerRepository = new UnregisteredCustomerRepository(entityManagerFactory);

        var dt = new DeliveryType();

        var productType1 = new ProductType();
        var productType2 = new ProductType();
        var productType3 = new ProductType();
        var productType4 = new ProductType();

        var product1 = new Product();
        var product2 = new Product();
        var product3 = new Product();
        var product4 = new Product();

        var u = new User();
        var pi = new User.PersonalInfo();
        var a1 = new Address();

        var uc = new UnregisteredCustomer();
        var a2 = new Address();

        var o1 = new Order();
        var o2 = new Order();

        var op1 = new OrderProducts();
        var op2 = new OrderProducts();
        var op3 = new OrderProducts();

        dt.setName("Nova Poshta");
        deliveryTypeRepository.insert(dt);

        productType1.setName("Grocery");
        productType2.setName("Bread");
        productType3.setName("Meat");
        productType4.setName("Toy");

        productTypeRepository.insert(productType1);
        productTypeRepository.insert(productType2);
        productTypeRepository.insert(productType3);
        productTypeRepository.insert(productType4);

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

        product4.setName("Super universal product");
        product4.setDescription("This product contains all the product types");
        product4.setPrice(new BigDecimal("777.77"));
        product4.setStock(777);
        product4.getProductTypes().add(productType1);
        product4.getProductTypes().add(productType2);
        product4.getProductTypes().add(productType3);
        product4.getProductTypes().add(productType4);

        productRepository.insert(product1);
        productRepository.insert(product2);
        productRepository.insert(product3);
        productRepository.insert(product4);

        pi.setPhoneNumber("0994824689");
        pi.setFirstname("Dmytro");
        u.setPersonalInfo(pi);
        u.setAddress(a1);
        u.setAccess(Access.ADMINISTRATOR);
        userRepository.insert(u);

        uc.setPhoneNumber("0964267234");
        uc.setFirstname("Maksym");
        a2.setRegion("reg");
        a2.setDistrict("dist");
        a2.setCity("city");
        a2.setStreet("st");
        a2.setBuilding(1);
        a2.setPostalCode(54123);
        uc.setAddress(a2);
        unregisteredCustomerRepository.insert(uc);

        o1.setUser(u);
        o1.setDeliveryType(dt);
        o1.setPostTime(new Timestamp(System.currentTimeMillis() - 10000));
        o1.setStatus(Status.POSTED);

        op1.setProduct(product1);
        op1.setQuantity(10);
        op1.setOrder(o1);

        op2.setProduct(product2);
        op2.setQuantity(5);
        op2.setOrder(o1);

        op3.setProduct(product3);
        op3.setQuantity(1);
        op3.setOrder(o1);

        o1.getOrderProducts().add(op1);
        o1.getOrderProducts().add(op2);
        o1.getOrderProducts().add(op3);

        o2.setUnregisteredCustomer(uc);
        o2.setDeliveryType(dt);
        o2.setPostTime(new Timestamp(System.currentTimeMillis() - 10000));
        o2.setStatus(Status.POSTED);

        // when
        var id1 = repoTest.insert(o1);
        var id2 = repoTest.insert(o2);

        var optInserted1 = repoTest.get(id1);
        var optInserted2 = repoTest.get(id2);

        assertThat(optInserted1.isPresent()).isTrue();
        assertThat(optInserted2.isPresent()).isTrue();

        var inserted1 = optInserted1.get();
        var inserted2 = optInserted2.get();

        // make some updates

        var orderProducts1 = inserted1.getOrderProducts();
        var orderProducts2 = inserted2.getOrderProducts();

        assertThat(new ArrayList<>(orderProducts1)).isEqualTo(o1.getOrderProducts());
        assertThat(new ArrayList<>(orderProducts2)).isEqualTo(o2.getOrderProducts());

        var dtAdd = new DeliveryType();
        dtAdd.setName("NEW DELIVERY TYPE");
        deliveryTypeRepository.insert(dtAdd);

        inserted1.setDeliveryType(dtAdd);
        inserted2.setDeliveryType(dtAdd);

        inserted1.setStatus(Status.ABOLISHED);
        inserted2.setStatus(Status.DELIVERED);

        inserted2.setCompleteTime(new Timestamp(System.currentTimeMillis() - 1000));

        orderProducts1.remove(0);
        orderProducts1.remove(0);

        var newOrderProducts = new OrderProducts();
        newOrderProducts.setProduct(product4);
        newOrderProducts.setQuantity(5);

        orderProducts2.add(newOrderProducts);

        // commit update
        var updId1 = repoTest.update(inserted1);
        var updId2 = repoTest.update(inserted2);

        var updatedOptional1 = repoTest.get(updId1);
        var updatedOptional2 = repoTest.get(updId2);

        assertThat(updatedOptional1.isPresent()).isTrue();
        assertThat(updatedOptional2.isPresent()).isTrue();

        var updated1 = updatedOptional1.get();
        var updated2 = updatedOptional2.get();

        var em = entityManagerFactory.createEntityManager();
        var orderProducts1FromDB = em
                .createQuery("select op from OrderProducts op where op.order.orderId != :id", OrderProducts.class)
                .setParameter("id", updated2.getOrderId())
                .getResultList();
        var orderProducts2FromDB = em
                .createQuery("select op from OrderProducts op where op.order.orderId != :id", OrderProducts.class)
                .setParameter("id", updated1.getOrderId())
                .getResultList();
        em.close();

        var orderProducts1FromRepository = updated1.getOrderProducts();
        var orderProducts2FromRepository = updated2.getOrderProducts();

        // then
        assertThat(new ArrayList<>(orderProducts1FromRepository)).isEqualTo(new ArrayList<>(orderProducts1FromDB));
        assertThat(updated1.getDeliveryType()).isEqualTo(inserted1.getDeliveryType());
        assertThat(updated1.getStatus()).isEqualTo((inserted1.getStatus()));
        assertThat(updated1.getCompleteTime()).isEqualTo(inserted1.getCompleteTime());

        assertThat(new ArrayList<>(orderProducts2FromRepository)).isEqualTo(new ArrayList<>(orderProducts2FromDB));
        assertThat(updated2.getDeliveryType()).isEqualTo(inserted2.getDeliveryType());
        assertThat(updated2.getStatus()).isEqualTo((inserted2.getStatus()));
        assertThat(updated2.getCompleteTime()).isEqualTo(inserted2.getCompleteTime());
    }
}
