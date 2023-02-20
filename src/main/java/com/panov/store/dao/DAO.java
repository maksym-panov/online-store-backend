package com.panov.store.dao;

import java.util.List;
import java.util.Optional;

public interface DAO<E> {

    Optional<E> get(int id);

    Optional<E> getByColumn(String columnName, String value);

    List<E> getAll();

    List<E> getWithEqualsFilter(String columnName, String value);

    void set(E entity);

    void update(E entity);

    void delete(int id);
}
