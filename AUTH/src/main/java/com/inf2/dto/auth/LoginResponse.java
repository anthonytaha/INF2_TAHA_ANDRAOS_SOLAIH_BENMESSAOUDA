package com.inf2.dto.auth;

public class LoginResponse {
    private String token;
    private String type = "Bearer";
    private long expiresIn;

    public LoginResponse(String token, long expiresIn) {
        this.token = token;
        this.expiresIn = expiresIn;
    }

    public String getToken() { return token; }
    public String getType() { return type; }
    public long getExpiresIn() { return expiresIn; }
}
