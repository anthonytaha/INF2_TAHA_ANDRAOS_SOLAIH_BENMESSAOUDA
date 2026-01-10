package com.inf2.service.auth;

import io.smallrye.jwt.build.Jwt;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import javax.enterprise.context.RequestScoped;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import java.util.Set;
import java.io.InputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.UUID;
import java.util.stream.Collectors;

@RequestScoped
public class TokenService {

    @ConfigProperty(name = "jwt.privatekey.location", defaultValue = "/privateKey-pkcs8.pem")
    private String privateKeyLocation;
    @ConfigProperty(name = "mp.jwt.verify.issuer", defaultValue = "https://ta-banque.com")
    private String issuer;
    @ConfigProperty(name = "mp.jwt.verify.keyId", defaultValue = "1")
    private String keyId;


    public String generate(UUID id, Set<String> groups) {
        try {
            // 1. Load the Private Key
            PrivateKey privateKey = readPrivateKey("/privateKey-pkcs8.pem");
            // 2. Build the Token
            String key = Jwt.issuer("https://ta-banque.com") // Must match mp.jwt.verify.issuer
                    .upn(id.toString())                // "User Principal Name" (Standard user field)
                    .subject(id.toString())
                    .groups(groups)               // Roles
                    .expiresIn(3600)              // Expires in 1 hour
                    .jws()
                    .keyId("1")
                    .sign(privateKey);            // Sign it!
            return key;
        } catch (Exception e) {
            throw new RuntimeException( e.getMessage());
        }
    }

    // Helper to read PEM file from classpath
    private PrivateKey readPrivateKey(String location) throws Exception {
        InputStream contentStream = getClass().getResourceAsStream(location);
        String content = new BufferedReader(new InputStreamReader(contentStream))
                .lines().collect(Collectors.joining("\n"));

        String privateKeyContent = content
                .replaceAll("-----BEGIN (.*)-----", "")
                .replaceAll("-----END (.*)-----", "")
                .replaceAll("\\s", "");

        byte[] decoded = Base64.getDecoder().decode(privateKeyContent);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePrivate(new PKCS8EncodedKeySpec(decoded));
    }
}