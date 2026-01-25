package com.inf2.service.domain;

import com.inf2.dao.ClientDAO;
import com.inf2.dao.impl.ClientDAOImpl;
import com.inf2.domain.Client;
import com.inf2.dto.Client.ClientCreateRequest;
import com.inf2.dto.auth.UserUpdateRequest;
import com.inf2.mapper.ClientMapper;
import com.inf2.messaging.UserCreatedProducer;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.mindrot.jbcrypt.BCrypt;

import java.util.List;
import java.util.UUID;

// step 3 : logique métier - où j'implémente les contraintes métiers
@Singleton
public class ClientService {

    @Inject
    private ClientDAO userDAO;
    @Inject
    private UserCreatedProducer userCreatedProducer;

    public Client getClientById(UUID id) {
        return userDAO.find(id);
    }
    public Client getClientByEmail(String email) {
        return userDAO.findByEmail(email);
    }
    public List<Client> getClients() {
        return userDAO.findAll();
    }
    public Client createClient(ClientCreateRequest clientCreateRequest) {

        Client user = ClientMapper.toClient(clientCreateRequest);

        String hashedPassword = BCrypt.hashpw(user.getPassword(), BCrypt.gensalt());
        user.setPassword(hashedPassword);
        Client createdClient =  userDAO.save(user);
        if(createdClient != null) {
            userCreatedProducer.sendUserCreatedEvent(createdClient);
        }
        return createdClient;
    }
    public void updateClient(UUID id, UserUpdateRequest userUpdateRequest){

        userDAO.update(id, userUpdateRequest.getEmail());
    }
    public void deleteClient(UUID id) {
        userDAO.delete(id);
    }


}
