package com.panov.store.model;

import com.panov.store.utils.Access;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@Entity
@Table(name = "\"User\"")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer userId;

    private String hashPassword;

    @Embedded
    private PersonalInfo personalInfo;

    @Embedded
    private Address address;

    @NotNull
    @Convert(converter = Access.AccessConverter.class)
    private Access access;

    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER)
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

    @Getter
    @Setter
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    @Embeddable
    public static class PersonalInfo {
        @NotNull(message = "Phone number must be present")
        @Pattern(regexp = "0\\d{9}", message = "Phone number must match the format '0XXXXXXXXX'")
        @Column(unique = true)
        private String phoneNumber;

        @Email
        @Size(max = 80, message = "Email is too long")
        @Column(unique = true)
        private String email;

        @NotNull(message = "FirstName must be present")
        @Size(min = 1, message = "Firstname cannot be empty")
        @Size(max = 30, message = "Firstname is too long")
        private String firstname;

        @Size(max = 30, message = "Firstname is too long")
        private String lastname;

        @Override
        public int hashCode() {
            return Objects.hash(email);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null) return false;
            if (!(o instanceof PersonalInfo other)) return false;
            return Objects.equals(phoneNumber, other.phoneNumber) &&
                    Objects.equals(email, other.email) &&
                    Objects.equals(firstname, other.firstname) &&
                    Objects.equals(lastname, other.lastname);
        }
    }
}
