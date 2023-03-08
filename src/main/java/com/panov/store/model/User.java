package com.panov.store.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.panov.store.utils.Access;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.NaturalId;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

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
    /// DELETE!!!!!!!!!!!
    private String hashPassword;
    @Embedded
    private PersonalInfo personalInfo;

    @Embedded
    private Address address;

    @OneToMany(mappedBy = "user")
    private Set<Order> orders = new HashSet<>();

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
        @NaturalId
        private String phoneNumber;
        private String email;
        private String firstname;
        private String lastname;

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
