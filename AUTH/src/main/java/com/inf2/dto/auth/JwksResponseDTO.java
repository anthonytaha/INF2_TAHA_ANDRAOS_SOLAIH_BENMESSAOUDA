package com.inf2.dto.auth;

import java.util.ArrayList;
import java.util.List;

public class JwksResponseDTO {
    // The spec requires the root element to be "keys"
    public List<JwkKeyDTO> keys = new ArrayList<>();

    public void addKey(JwkKeyDTO key) {
        this.keys.add(key);
    }
}
