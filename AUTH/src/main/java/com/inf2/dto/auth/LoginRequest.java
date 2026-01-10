package com.inf2.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

public class LoginRequest {

    @NotNull(message = "Email cannot be empty")
    @Email (message = "Please provide a valid email")
    private String email;

    @NotNull(message = "Password cannot be empty")
    private String password;

    public String getEmail() { return email; }
    public void setEmail(String username) { this.email = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}