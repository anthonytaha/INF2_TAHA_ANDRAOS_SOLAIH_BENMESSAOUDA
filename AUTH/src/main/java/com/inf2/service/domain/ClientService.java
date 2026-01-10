package com.inf2.service.domain;

import com.inf2.dao.ClientDAO;
import com.inf2.dao.impl.ClientDAOImpl;
import com.inf2.domain.Client;
import com.inf2.dto.Client.ClientCreateRequest;
import com.inf2.dto.auth.UserUpdateRequest;
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

        Client user = new Client(
                clientCreateRequest.getFirstName(),
                clientCreateRequest.getLastName(),
                clientCreateRequest.getEmail(),
                clientCreateRequest.getPassword(),
                clientCreateRequest.getDateOfBirth(),
                clientCreateRequest.getPhoneNumber()
        );

        String hashedPassword = BCrypt.hashpw(user.getPassword(), BCrypt.gensalt());
        user.setPassword(hashedPassword);
        return userDAO.save(user);
    }
    public void updateClient(UUID id, UserUpdateRequest userUpdateRequest){

        userDAO.update(id, userUpdateRequest);
    }
    public void deleteClient(UUID id) {
        userDAO.delete(id);
    }


}
