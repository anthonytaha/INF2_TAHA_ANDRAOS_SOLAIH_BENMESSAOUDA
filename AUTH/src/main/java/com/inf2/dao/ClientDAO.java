package com.inf2.dao;

import com.inf2.domain.Client;
import com.inf2.dto.auth.UserUpdateRequest;

import java.util.List;
import java.util.UUID;

public interface ClientDAO {
    List<Client> findAll();
    Client find(UUID id);
    Client findByEmail(String email) ;
    Client save(Client user);
    void update(UUID id, String email);
    void delete(UUID id);
}
