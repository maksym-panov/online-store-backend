package com.panov.store.utils;

import jakarta.persistence.AttributeConverter;

public enum Access {
    USER("U"),
    MANAGER("M"),
    ADMINISTRATOR("A");

    private final String code;

    Access(String code) {
        this.code = code;
    }

    public static Access fromCode(String code) {
        if (code.equals("U")) return USER;
        if (code.equals("M")) return MANAGER;
        if (code.equals("A")) return ADMINISTRATOR;

        throw new IllegalArgumentException();
    }

    public String getCode() {
        return code;
    }

    public static class AccessConverter implements AttributeConverter<Access, String> {

        @Override
        public String convertToDatabaseColumn(Access access) {
            return access == null ? null : access.getCode();
        }

        @Override
        public Access convertToEntityAttribute(String code) {
            return code == null ? null : fromCode(code);
        }
    }
}
