package com.panov.store.services;

import com.panov.store.dao.DAO;
import com.panov.store.dao.DeliveryTypeRepository;
import com.panov.store.exceptions.ResourceNotCreatedException;
import com.panov.store.exceptions.ResourceNotDeletedException;
import com.panov.store.exceptions.ResourceNotFoundException;
import com.panov.store.exceptions.ResourceNotUpdatedException;
import com.panov.store.model.DeliveryType;
import com.panov.store.model.ProductType;
import com.panov.store.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service-layer class that processes {@link DeliveryType} entities.
 *
 * @author Maksym Panov
 * @version 1.0
 * @see DAO
 * @see DeliveryTypeRepository
 */
@Service
public class DeliveryTypeService {
    private final DAO<DeliveryType> repository;

    @Autowired
    public DeliveryTypeService(DAO<DeliveryType> repository) {
        this.repository = repository;
    }

    /**
     * Uses {@link DAO} implementation to retrieve list of all existing {@link DeliveryType} entities. <br><br>
     * Re-throws a {@link ResourceNotFoundException} if {@link DAO} object throws an exception.
     *
     * @return a {@link List} of {@link DeliveryType} objects
     */
    public List<DeliveryType> getDeliveryTypeList() {
        try {
            var list = repository.getAll();
            if (list == null)
                return Collections.emptyList();
            return list;
        } catch(Exception e) {
            e.printStackTrace();
            throw new ResourceNotFoundException("Could not find delivery types");
        }
    }

    /**
     * Uses {@link DAO} implementation to retrieve a {@link DeliveryType} entity by specified identity. <br><br>
     * Re-throws a {@link ResourceNotFoundException} if {@link DAO} object throws an exception or there is
     * no such {@link DeliveryType} object.
     *
     * @param id an identity of the sought {@link DeliveryType}
     * @return a {@link DeliveryType} object with specified identity
     */
    public DeliveryType getById(Integer id) {
        return repository.get(id).orElseThrow(() ->
                new ResourceNotFoundException("Could not find this delivery type")
        );
    }

    /**
     * Searches for {@link DeliveryType} objects using {@link DAO} implementation by specified
     * name pattern. <br><br>
     * Re-throws a {@link ResourceNotFoundException} if {@link DAO} object throws an exception.
     *
     * @param namePattern a name pattern you want to search by
     * @param strict if {@code true} returns exact matches and if {@code false}
     *               returns a list of {@link ProductType} objects whose names contain
     *               {@code namePattern} as their part (case-insensitive)
     * @return a list of {@link ProductType} objects that match specified pattern
     */
    public List<DeliveryType> getByNamePattern(String namePattern, boolean strict) {
        try {
            var list = repository.getByColumn(namePattern, strict);
            if (list == null)
                return Collections.emptyList();
            return list;
        } catch(Exception e) {
            e.printStackTrace();
            throw new ResourceNotFoundException("Could not find delivery types");
        }
    }

    /**
     * Uses {@link DAO} implementation to save new {@link DeliveryType} in the data storage. <br><br>
     * Only {@link User} with {@code Access.ADMINISTRATOR} or {@code Access.MANAGER} authority
     * can invoke this method. <br><br>
     * Re-throws a {@link ResourceNotCreatedException} if {@link DAO} object throws an exception.
     *
     * @param deliveryType an object to save
     * @return an identity of saved {@link DeliveryType}
     */
    @PreAuthorize("hasAuthority('ADMINISTRATOR') or hasAuthority('MANAGER')")
    public Integer createDeliveryType(DeliveryType deliveryType) {
        Map<String, String> matches = thisNaturalIdExists(deliveryType);
        if (matches.size() != 0)
            throw new ResourceNotCreatedException(matches);

        Integer id = null;

        try {
            id = repository.insert(deliveryType);
        } catch(Exception e) {
            e.printStackTrace();
        }

        if (id == null)
            throw new ResourceNotCreatedException("Could not create this delivery type");

        return id;
    }


    /**
     * Uses {@link DAO} implementation to change information of {@link DeliveryType}. <br><br>
     * Only {@link User} with {@code Access.ADMINISTRATOR} or {@code Access.MANAGER} authority
     * can invoke this method. <br><br>
     * Re-throws a {@link ResourceNotUpdatedException} if {@link DAO} object throws an exception.
     *
     * @param deliveryType an object that contains new data and identity of {@link DeliveryType}
     *                     that should be updated
     * @return an identity of updated {@link DeliveryType}
     */
    @PreAuthorize("hasAuthority('ADMINISTRATOR') or hasAuthority('MANAGER')")
    public Integer changeDeliveryType(DeliveryType deliveryType) {
        Map<String, String> matches = thisNaturalIdExists(deliveryType);
        if (matches.size() != 0)
            throw new ResourceNotUpdatedException(matches);

        Integer id = null;

        try {
            id = repository.update(deliveryType);
        } catch(Exception e) {
            e.printStackTrace();
        }

        if (id == null)
            throw new ResourceNotUpdatedException("Could not change this delivery type");

        return id;
    }

    /**
     * Uses {@link DAO} to remove the {@link DeliveryType} object with specified identity from data storage. <br><br>
     * Only {@link User} with {@code Access.ADMINISTRATOR} authority can invoke this method. <br><br>
     * Re-throws a {@link ResourceNotDeletedException} if {@link DAO} object throws an exception.
     *
     * @param id an identity of {@link DeliveryType} that should be deleted
     */
    @PreAuthorize("hasAuthority('ADMINISTRATOR')")
    public void deleteDeliveryType(Integer id) {
        try {
            repository.delete(id);
        } catch(Exception e) {
            e.printStackTrace();
            throw new ResourceNotDeletedException("Could not delete this delivery type");
        }
    }

    /**
     * Checks if the name of provided object is already used in existing {@link DeliveryType} object.
     *
     * @param deliveryType an object to check
     * @return a {@link Map} that contains decision about uniqueness of the name of specified {@link DeliveryType}
     */
    private Map<String, String> thisNaturalIdExists(DeliveryType deliveryType) {
        Map<String, String> matches = new HashMap<>();

        try {
            var nameMatch = getByNamePattern(deliveryType.getName(), true);
            if (nameMatch.size() != 0)
                matches.put("name", "Delivery type with this name already exists");
        } catch(Exception ignored) {}

        return matches;
    }
}
