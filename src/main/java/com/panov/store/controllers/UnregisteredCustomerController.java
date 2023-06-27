package com.panov.store.controllers;

import com.panov.store.dto.UnregisteredCustomerDTO;
import com.panov.store.exceptions.ResourceNotUpdatedException;
import com.panov.store.model.UnregisteredCustomer;
import com.panov.store.services.UnregisteredCustomerService;
import com.panov.store.utils.ListUtils;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Web controller that handles requests associated with {@link UnregisteredCustomer}. <br>
 *
 * @author Maksym Panov
 * @version 2.0
 * @see UnregisteredCustomerDTO
 * @see UnregisteredCustomerService
 */
@RestController
@RequestMapping("/api/v2/unregistered_customers")
public class UnregisteredCustomerController {
    private final UnregisteredCustomerService service;

    @Autowired
    public UnregisteredCustomerController(UnregisteredCustomerService service) {
        this.service = service;
    }

    /**
     * Returns a list of all {@link UnregisteredCustomer} objects. There is <br>
     * also a possibility of using parameters in the request link <br>
     * to customize the output. <br><br>
     * HTTP method: {@code GET} <br>
     * Endpoint: {@code /unregistered_customers{?phoneNumber=&quantity=&offset=}} <br>
     *
     * @param quantity if specified, the method will return only the first
     *                 {@code quantity} unregistered customers.
     * @param offset if specified, the method will skip first {@code offset}
     *               unregistered customers.
     * @return a list of {@link UnregisteredCustomer} objects.
     */
    @GetMapping
    public List<UnregisteredCustomer> unregisteredCustomersRange(
            @RequestParam(name = "quantity", required = false) Integer quantity,
            @RequestParam(name = "offset", required = false) Integer offset) {
        return ListUtils.makeCut(service.getUnregCustomerList(), quantity, offset);
    }

    /**
     * Retrieves a {@link UnregisteredCustomer} with specified ID. <br><br>
     * HTTP method: {@code GET} <br>
     * Endpoint: {@code /unregistered_customers/{unregisteredCustomerId}} <br>
     *
     * @param id an identifier of a {@link UnregisteredCustomer}
     * @return retrieved unregistered customer instance with specified identifier
     */
    @GetMapping("/{id}")
    public UnregisteredCustomer specificUnregisteredCustomer(@PathVariable("id") Integer id) {
        return service.getById(id);
    }

    /**
     * Changes information of {@link UnregisteredCustomer} object with specified ID. <br><br>
     * Http method: {@code PATCH} <br>
     * Endpoint: /unregistered_customers/{unregisteredCustomerId} <br>
     *
     * @param unregCustDTO a data transfer object for {@link UnregisteredCustomer}.
     * @param id an identifier of an unregistered customer which user wants to change.
     * @param bindingResult a Hibernate Validator object which keeps all
     *                      validation violations.
     * @return an identifier of provided {@link UnregisteredCustomer}.
     */
    @PatchMapping("/{id}")
    public Integer changeUnregisteredCustomer(@RequestBody @Valid UnregisteredCustomerDTO unregCustDTO,
                                              @PathVariable("id") Integer id,
                                              BindingResult bindingResult) {
        if (bindingResult.hasErrors())
            throw new ResourceNotUpdatedException(bindingResult);

        unregCustDTO.setUnregisteredCustomerId(id);

        return service.changeUnregisteredCustomer(unregCustDTO.toModel());
    }
}
