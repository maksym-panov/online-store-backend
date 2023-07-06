package com.panov.store.common;

import com.panov.store.model.Order;
import jakarta.persistence.AttributeConverter;

/**
 * This enumeration represents statuses of the {@link Order}.
 *
 * @author Maksym Panov
 * @version 1.0
 */
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

    /**
     * Converts enumeration objects into strings to save in the data storage.
     *
     * @author Maksym Panov
     * @version 1.0
     */
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
