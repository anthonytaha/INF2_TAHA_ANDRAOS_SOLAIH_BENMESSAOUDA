package com.inf2.dao;

import com.inf2.domain.HelloMessage;
import jakarta.persistence.EntityManager;

public interface HelloDAO {
    HelloMessage save(HelloMessage message);
}