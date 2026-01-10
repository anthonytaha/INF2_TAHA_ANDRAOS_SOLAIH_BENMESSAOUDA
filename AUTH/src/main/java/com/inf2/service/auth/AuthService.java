package com.inf2.service.auth;

import com.inf2.domain.Advisor;
import com.inf2.domain.Client;
import com.inf2.dto.auth.LoginRequest;
import com.inf2.dto.auth.LoginResponse;
import com.inf2.service.domain.AdvisorService;
import com.inf2.service.domain.ClientService;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.mindrot.jbcrypt.BCrypt;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Singleton
public class AuthService {

    @Inject
    private ClientService clientService;

    @Inject
    private AdvisorService advisorService;

    @Inject
    private TokenService tokenService;

    public LoginResponse clientLogin(LoginRequest loginRequest) {
        Client client = clientService.getClientByEmail(loginRequest.getEmail());
        if(client == null) {
            throw new RuntimeException("Client with email " + loginRequest.getEmail() + " not found");
        }else if(!BCrypt.checkpw(loginRequest.getPassword(), client.getPassword())){
            throw new RuntimeException("Wrong password");
        }
        Set<String> roles = new HashSet<>(Collections.singletonList("client"));
        String token = tokenService.generate(client.getId(), roles);
        return new LoginResponse(token,3600);
    }

    public LoginResponse advisorLogin(LoginRequest loginRequest) {
        Advisor advisor = advisorService.getAdvisorByEmail(loginRequest.getEmail());
        if(advisor == null) {
            throw new RuntimeException("Advisor with email " + loginRequest.getEmail() + " not found");
        }else if(!BCrypt.checkpw(loginRequest.getPassword(), advisor.getPassword())){
            throw new RuntimeException("Wrong password");
        }
        Set<String> roles = new HashSet<>(Collections.singletonList("advisor"));
        String token = tokenService.generate(advisor.getId(), roles);
        return new LoginResponse(token,3600);
    }


}
