package com.panov.store.services;

import com.panov.store.dao.DAO;
import com.panov.store.dao.UnregisteredCustomerRepository;
import com.panov.store.exceptions.ResourceNotCreatedException;
import com.panov.store.exceptions.ResourceNotFoundException;
import com.panov.store.exceptions.ResourceNotUpdatedException;
import com.panov.store.model.UnregisteredCustomer;
import com.panov.store.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

/**
 * Service-layer class that processes {@link UnregisteredCustomer} entities.
 *
 * @author Maksym Panov
 * @version 1.0
 * @see DAO
 * @see UnregisteredCustomerRepository
 */
@Service
public class UnregisteredCustomerService {
    private final DAO<UnregisteredCustomer> repository;

    @Autowired
    public UnregisteredCustomerService(DAO<UnregisteredCustomer> repository) {
        this.repository = repository;
    }

    /**
     * Uses {@link DAO} implementation to retrieve list of all existing {@link UnregisteredCustomer} entities. <br><br>
     * Only {@link User} with {@code Access.ADMINISTRATOR} or {@code Access.MANAGER} authority
     * can invoke this method.
     * Re-throws a {@link ResourceNotFoundException} if {@link DAO} object throws an exception.
     *
     * @return a {@link List} of {@link UnregisteredCustomer} objects
     */
    @PreAuthorize("hasAuthority('ADMINISTRATOR') or hasAuthority('MANAGER')")
    public List<UnregisteredCustomer> getUnregCustomerList(Integer offset, Integer quantity) {
        try {
            var list = repository.getPackage(offset, quantity);
            if (list == null)
                return Collections.emptyList();
            return list;
        } catch(Exception e) {
            e.printStackTrace();
            throw new ResourceNotFoundException("Could not find unregistered customers");
        }
    }

    /**
     * Uses {@link DAO} implementation to retrieve a {@link UnregisteredCustomer} entity by specified identity. <br><br>
     * Only {@link User} with {@code Access.ADMINISTRATOR} or {@code Access.MANAGER} authority
     * can invoke this method. <br><br>
     * Re-throws a {@link ResourceNotFoundException} if {@link DAO} object throws an exception or there is
     * no such {@link UnregisteredCustomer} object.
     *
     * @param id an identity of the sought {@link UnregisteredCustomer}
     * @return a {@link UnregisteredCustomer} object with specified identity
     */
    @PreAuthorize("hasAuthority('ADMINISTRATOR') or hasAuthority('MANAGER')")
    public UnregisteredCustomer getById(Integer id) {
        return repository.get(id).orElseThrow(() -> new ResourceNotFoundException("Could not find this unregistered customer"));
    }

    /**
     * Uses {@link DAO} implementation to save new {@link UnregisteredCustomer} in the data storage. <br><br>
     * Re-throws a {@link ResourceNotCreatedException} if {@link DAO} object throws an exception.
     *
     * @param unregCust an object to save
     */
    public void createUnregisteredCustomer(UnregisteredCustomer unregCust) {
        Integer id = null;

        try {
            id = repository.insert(unregCust);
        } catch(Exception e) {
            e.printStackTrace();
        }

        if (id == null)
            throw new ResourceNotCreatedException("Could not create this unregistered customer");
    }

    /**
     * Uses {@link DAO} implementation to change information of {@link UnregisteredCustomer}. <br><br>
     * Only {@link User} with {@code Access.ADMINISTRATOR} or {@code Access.MANAGER} authority
     * can invoke this method. <br><br>
     * Re-throws a {@link ResourceNotUpdatedException} if {@link DAO} object throws an exception.
     *
     * @param unregCust an object that contains new data and identity of {@link UnregisteredCustomer}
     *                     that should be updated
     * @return an identity of updated {@link UnregisteredCustomer}
     */
    @PreAuthorize("hasAuthority('ADMINISTRATOR') or hasAuthority('MANAGER')")
    public Integer changeUnregisteredCustomer(UnregisteredCustomer unregCust) {
        Integer id = null;

        try {
            id = repository.update(unregCust);
        } catch(Exception e) {
            e.printStackTrace();
        }

        if (id == null)
            throw new ResourceNotUpdatedException("Could not change this unregistered customer");

        return id;
    }
}
