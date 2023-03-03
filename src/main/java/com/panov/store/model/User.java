package com.panov.store.model;

import com.panov.store.utils.Access;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Entity
@Table(name = "\"User\"")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer userId;

    @Embedded
    private PersonalInfo personalInfo;

    @Embedded
    private Address address;

    protected User() {}

    @Override
    public String toString() {
        return String.format("User ID: %d %n %s %s",
                userId,
                personalInfo.toString(),
                address.toString()
        );
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @Embeddable
    public static class PersonalInfo {
        private String phoneNumber;
        private String email;
        private String firstname;
        private String lastname;

        @Convert(converter = Access.AccessConverter.class)
        private Access access;

        protected PersonalInfo() {}

        @Override
        public String toString() {
            return String.format("%s %s %n Tel.: %s %n Email: %s %n Role: %s %n %n",
                    firstname,
                    lastname,
                    phoneNumber,
                    email,
                    access
            );
        }
    }
}
