package com.inf2.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Table(name = "clients")
public class Client extends User {

    private LocalDateTime joinDate;

    @NotNull(message = "Phone number is required")
    @Pattern(regexp = "^\\+?[0-9 \\-()]{7,}$", message = "Invalid phone number format")
    private String phoneNumber;

    public Client(String firstName, String lastName, String email, String password, Date dateOfBirth, String phoneNumber) {
        super(firstName, lastName, email, password, dateOfBirth);
        this.joinDate = LocalDateTime.now();
        this.phoneNumber = phoneNumber;
    }

    public Client() {
        super();
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public LocalDateTime getJoinDate() {
        return joinDate;
    }

}
