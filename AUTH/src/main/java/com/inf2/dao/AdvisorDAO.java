package com.inf2.dao;

import com.inf2.domain.Advisor;
import com.inf2.dto.auth.UserUpdateRequest;

import java.util.UUID;

public interface AdvisorDAO {
    Advisor find(UUID id);
    Advisor findByEmail(String email);
    Advisor save(Advisor user);
    void    update(UUID id, UserUpdateRequest userUpdateRequest);
    void    delete(UUID id);
}
