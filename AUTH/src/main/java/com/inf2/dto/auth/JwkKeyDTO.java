package com.inf2.dto.auth;

public class JwkKeyDTO {
    public String kty = "RSA";
    public String use = "sig";
    public String alg = "RS256";
    public String kid; // Key ID
    public String n;   // Modulus (Base64URL encoded)
    public String e;   // Exponent (Base64URL encoded)

    // Empty constructor for serialization frameworks
    public JwkKeyDTO() {}

    public JwkKeyDTO(String kid, String n, String e) {
        this.kid = kid;
        this.n = n;
        this.e = e;
    }
}
