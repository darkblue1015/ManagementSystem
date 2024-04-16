package com.group.eBookManagementSystem.model;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Setter
@Getter
public class Customer {
    // initialize the attributes
    @Id
    private String userName;
    private String firstName;
    private String lastName;
    private String password;
    private String email;
    @ElementCollection
    private List<Integer> myLibrary;
    @Enumerated(EnumType.STRING)
    private Role role;

    // an enum representing the roles that a customer can have
    public enum Role {
        ADMIN,
        USER
    }

}
