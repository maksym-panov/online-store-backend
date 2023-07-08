package com.panov.store.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class AuthEntity {
    private String jwt;
    private Integer userId;
}
