package com.panov.store.controllers;

import com.panov.store.dto.UnregisteredCustomerDTO;
import com.panov.store.exceptions.ResourceNotCreatedException;
import com.panov.store.exceptions.ResourceNotUpdatedException;
import com.panov.store.model.UnregisteredCustomer;
import com.panov.store.services.UnregisteredCustomerService;
import com.panov.store.utils.ListUtils;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
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
    public List<UnregisteredCustomer> unregisteredCustomersRange(
            @RequestBody(required = false) String phoneNumber,
            @RequestParam(name = "quantity", required = false) Integer quantity,
            @RequestParam(name = "offset", required = false) Integer offset) {
        if (phoneNumber == null)
            return ListUtils.makeCut(service.getUnregCustomerList(), quantity, offset);
        return service.getByPhoneNumber(phoneNumber);
    }

    @GetMapping("/{id}")
    public UnregisteredCustomer specificUnregisteredCustomer(@PathVariable("id") Integer id) {
        return service.getById(id);
    }

    @PostMapping
    public Integer createUnregisteredCustomer(@RequestBody @Valid UnregisteredCustomerDTO unregCustDTO,
                                              BindingResult bindingResult) {
        if (bindingResult.hasErrors())
            throw new ResourceNotCreatedException(bindingResult);

        return service.createUnregisteredCustomer(unregCustDTO.toModel());
    }

    @PatchMapping("/{id}")
    public Integer changeUnregisteredCustomer(@RequestBody @Valid UnregisteredCustomerDTO unregCustDTO,
                                              @PathVariable("id") Integer id,
                                              BindingResult bindingResult) {
        if (bindingResult.hasErrors())
            throw new ResourceNotUpdatedException(bindingResult);

        unregCustDTO.setUnregisteredCustomerId(id);

        return service.changeUnregisteredCustomer(unregCustDTO.toModel());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteUnregisteredCustomer(@PathVariable("id") Integer id) {
        service.deleteUnregisteredCustomer(id);
        return ResponseEntity.ok(HttpStatus.OK);
    }

}
