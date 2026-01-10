package com.inf2.resource;

import com.inf2.dto.auth.JwkKeyDTO;
import com.inf2.dto.auth.JwksResponseDTO;
import com.inf2.dto.auth.LoginRequest;
import com.inf2.service.auth.AuthService;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.KeyFactory;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.stream.Collectors;

@Path("/auth")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AuthResource {

    String publicKeyLocation = "/publicKey.pem";

    @Inject
    private AuthService authService;

    @POST
    @Path("/client/login")
    public Response loginClient(@Valid LoginRequest credentials) {
        try{
            return Response.ok(authService.clientLogin(credentials)).build();
        }catch (RuntimeException e) {
            return Response.status(Response.Status.UNAUTHORIZED).entity(e.getMessage()).build();
        }
    }

    @POST
    @Path("/advisor/login")
    public Response loginAdvisor(@Valid LoginRequest credentials) {
        try{
            return Response.ok(authService.advisorLogin(credentials)).build();
        }catch (RuntimeException e) {
            return Response.status(Response.Status.UNAUTHORIZED).entity(e.getMessage()).build();
        }
    }


}
