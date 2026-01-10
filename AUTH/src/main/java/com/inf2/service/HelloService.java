package com.inf2.service;

import com.inf2.dao.HelloDAO;
import com.inf2.domain.HelloMessage;
import jakarta.inject.Inject;
import jakarta.inject.Provider; // <--- Use Provider for Scope Safety
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

@jakarta.inject.Singleton
public class HelloService {

    @Inject
    private HelloDAO helloDAO;

    public String saveGreeting(String name) {
        String greeting = String.format("Hello, %s! Data saved via the Service and DAO Layers.", name != null ? name : "User");
        return helloDAO.save(new HelloMessage(greeting)).getContent();
    }
}