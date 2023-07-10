package com.panov.store.dao;

import java.util.List;
import java.util.Optional;

/**
 * Interface that represents data access level of the application.
 *
 * @param <E>
 * @author Maksym Panov
 * @version 1.0
 */
public interface DAO<E> {

    /**
     * Retrieves an entity from the data storage by its identifier.
     *
     * @param id an identifier of the entity which user wants to retrieve
     * @return optional of the sought entity
     */
    Optional<E> get(int id);

    /**
     * Retrieves a list of all entities from the data storage.
     *
     * @param offset sets the first entity from which method will fetch
     *               products that match the value
     * @param quantity the maximal number of entities that will be fetched
     * @return a list of all entities
     */
    List<E> getPackage(Integer offset, Integer quantity);

    /**
     * Retrieves a list of entities whose match the provided pattern.
     *
     * @param value a pattern for choosing objects, may be string.
     * @param strict if true, method should search for exact equality and
     *               if false, method should see {@code value} as a part of
     *               object field (e.g. part of name)
     * @param offset sets the first entity from which method will fetch
     *               products that match the value
     * @param quantity the maximal number of entities that will be fetched
     * @return a list of entities that match the pattern
     */
    List<E> getByColumn(Object value, Integer offset, Integer quantity, boolean strict);

    /**
     * Adds new entity and saves it.
     *
     * @param entity an entity to save
     * @return an identity of saved entity
     */
    Integer insert(E entity);

    /**
     * Changes information about existing entity.
     *
     * @param entity an object with update information.
     * @return an identity of updated entity.
     */
    Integer update(E entity);

    /**
     * Deletes existing entity by its identity.
     *
     * @param id an identity of the entity to be deleted
     */
    void delete(Integer id);

}
