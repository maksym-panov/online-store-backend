package com.panov.store.controllers;

import com.panov.store.model.UnregisteredCustomer;
import com.panov.store.services.UnregisteredCustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/unregistered_customers")
public class UnregisteredCustomerController {
    private final UnregisteredCustomerService service;

    @Autowired
    public UnregisteredCustomerController(UnregisteredCustomerService service) {
        this.service = service;
    }

    @GetMapping
    public List<UnregisteredCustomer> unregisteredCustomersRange() {
        return service.getUnregCustomerList();
    }

    @GetMapping("/{id}")
    public UnregisteredCustomer specificUnregisteredCustomer(@PathVariable("id") Integer id) {
        return service.getById(id);
    }
}
