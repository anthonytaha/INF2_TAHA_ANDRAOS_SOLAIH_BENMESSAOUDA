package com.inf2.dto.auth;


public class UserUpdateRequest {
    private String email;

    public UserUpdateRequest() {}

    public UserUpdateRequest(String email) {
        this.email = email;
    }


    public String getEmail() {
        return email;
    }

}
