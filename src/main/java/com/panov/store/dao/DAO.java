package com.panov.store.dao;

import java.util.List;
import java.util.Optional;

public interface DAO<E> {

    Optional<E> get(int id);

    Optional<E> getByColumn(String columnName, String value);

    List<E> getAll();

    Integer insert(E entity);

    Integer update(E entity);

    void delete(E entity);
}
