package com.panov.store.utils;

import jakarta.persistence.AttributeConverter;

public enum Status {
    POSTED("P"),
    ACCEPTED("A"),
    SHIPPING("S"),
    DELIVERED("D"),
    COMPLETED("C"),
    ABOLISHED("H");

    private final String code;

    Status(String code) {
        this.code = code;
    }

    public static Status fromCode(String code) {
        if (code.equals("P")) return POSTED;
        if (code.equals("A")) return ACCEPTED;
        if (code.equals("S")) return SHIPPING;
        if (code.equals("D")) return DELIVERED;
        if (code.equals("C")) return COMPLETED;
        if (code.equals("H")) return ABOLISHED;

        throw new IllegalArgumentException();
    }

    public String getCode() {
        return code;
    }

    public static class StatusConverter implements AttributeConverter<Status, String> {

        @Override
        public String convertToDatabaseColumn(Status status) {
            return status == null ? null : status.getCode();
        }

        @Override
        public Status convertToEntityAttribute(String code) {
            return code == null ? null : fromCode(code);
        }
    }
}
