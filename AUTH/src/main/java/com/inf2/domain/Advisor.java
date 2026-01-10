package com.inf2.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

import java.util.Date;

@Entity
@Table(name = "advisors")
public class Advisor extends User{

    @NotNull (message = "Department Code is required")
    private String departmentCode;

    public Advisor(String firstName, String lastName, String email, String password, Date dateOfBirth, String departmentCode) {
        super(firstName, lastName, email, password, dateOfBirth);
        this.departmentCode = departmentCode;
    }

    public Advisor() {
        super();
    }
}
