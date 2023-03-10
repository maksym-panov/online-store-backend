package com.panov.store.exceptions.products;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.validation.BindingResult;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductNotCreatedException extends RuntimeException {
    private BindingResult bindingResult;
}
