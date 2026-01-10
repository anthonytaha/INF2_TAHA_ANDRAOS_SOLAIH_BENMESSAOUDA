package com.inf2.domain;


import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

import java.util.Date;
import java.util.UUID;

@MappedSuperclass
public class User { //step 1 : define what user entity looks like
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotNull (message = "First name is required")
    private String firstName;

    @NotNull (message = "Last name is required")
    private String lastName;

    @NotNull (message = "Email is required")
    @Email (message = "Please provide a valid email address")
    private String email;

    @NotNull (message = "Password is required")
    private String password;

    @NotNull (message = "Date of  birth is required")
    private Date dateOfBirth;

    public User() {

    }

    public User(String firstName, String lastName, String email, String password, Date dateOfBirth) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.dateOfBirth = dateOfBirth;
    }

    public UUID getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }
}
