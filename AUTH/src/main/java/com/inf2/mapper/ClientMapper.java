package com.inf2.mapper;

import com.inf2.domain.Client;
import com.inf2.dto.Client.ClientCreateRequest;

public class ClientMapper {
    public static Client toClient(ClientCreateRequest clientCreateRequest) {
        return new Client(
            clientCreateRequest.getFirstName(),
            clientCreateRequest.getLastName(),
            clientCreateRequest.getEmail(),
            clientCreateRequest.getPassword(),
            clientCreateRequest.getDateOfBirth(),
            clientCreateRequest.getPhoneNumber()
        );
    }
}
