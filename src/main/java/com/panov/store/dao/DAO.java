package com.panov.store.dao;

import java.util.List;
import java.util.Optional;

public interface DAO<E> {

    Optional<E> get(int id);

    List<E> getAll();

    List<E> getByColumn(Object value, boolean strict);

    Integer insert(E entity);

    Integer update(E entity);

    void delete(Integer id);

}
