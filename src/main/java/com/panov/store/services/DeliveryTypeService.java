package com.panov.store.services;

import com.panov.store.dao.DAO;
import com.panov.store.exceptions.ResourceNotCreatedException;
import com.panov.store.exceptions.ResourceNotDeletedException;
import com.panov.store.exceptions.ResourceNotFoundException;
import com.panov.store.exceptions.ResourceNotUpdatedException;
import com.panov.store.model.DeliveryType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DeliveryTypeService {
    private final DAO<DeliveryType> repository;

    @Autowired
    public DeliveryTypeService(DAO<DeliveryType> repository) {
        this.repository = repository;
    }

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

    public DeliveryType getById(Integer id) {
        return repository.get(id).orElseThrow(() -> new ResourceNotFoundException("Could not find this delivery type"));
    }

    public List<DeliveryType> getByNamePattern(Object value, boolean strict) {
        try {
            var list = repository.getByColumn(value, strict);
            if (list == null)
                return Collections.emptyList();
            return list;
        } catch(Exception e) {
            e.printStackTrace();
            throw new ResourceNotFoundException("Could not find delivery types");
        }
    }

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

    public void deleteDeliveryType(Integer id) {
        try {
            repository.delete(id);
        } catch(Exception e) {
            e.printStackTrace();
            throw new ResourceNotDeletedException("Could not delete this delivery type");
        }
    }

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
