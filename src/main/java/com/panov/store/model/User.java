package com.panov.store.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.panov.store.utils.Access;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.NaturalId;

import java.util.*;

@Getter
@Setter
@NoArgsConstructor
@Entity(name = "User")
@Table(name = "\"User\"")
@JsonIgnoreProperties({ "hashPassword", "orders" })
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer userId;

    private String hashPassword;

    @Embedded
    private PersonalInfo personalInfo;

    @Embedded
    private Address address;

    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<Order> orders = new ArrayList<>();

    @Override
    public int hashCode() {
        Objects.requireNonNull(personalInfo);
        return personalInfo.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        if (!(o instanceof User other)) return false;
        return Objects.equals(this.personalInfo, other.personalInfo);
    }

    @Override
    public String toString() {
        return String.format("User[Id = %d %s %s]",
                userId,
                personalInfo,
                address
        );
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @Embeddable
    public static class PersonalInfo {
        @NotNull(message = "Phone number must be present")
        @Pattern(regexp = "0\\d{9}", message = "Phone number must match the format '0XXXXXXXXX'")
        @NaturalId
        private String phoneNumber;

        @Email
        @Size(max = 80, message = "Email is too long")
        @NaturalId
        private String email;

        @NotNull(message = "FirstName must be present")
        @Size(min = 1, message = "Firstname cannot be empty")
        @Size(max = 30, message = "Firstname is too long")
        private String firstname;
        @Size(max = 30, message = "Firstname is too long")
        private String lastname;

        @NotNull
        @Convert(converter = Access.AccessConverter.class)
        private Access access;

        @Override
        public int hashCode() {
            return Objects.hash(email);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null) return false;
            if (!(o instanceof PersonalInfo other)) return false;
            return Objects.equals(this.phoneNumber, other.phoneNumber);
        }

        @Override
        public String toString() {
            return String.format("PersonalInfo[%s %s Tel.: %s Email: %s Role: %s]",
                    firstname,
                    lastname,
                    phoneNumber,
                    email,
                    access
            );
        }
    }
}
