package com.inf2;

import jakarta.inject.Singleton;
import jakarta.ws.rs.*;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.*;

import java.io.InputStream;

@Path("/")
@Singleton
public class GatewayResource {

    private final Client client;

    // Configuration of backend URLs
    private static final String AUTH_SERVICE = "http://localhost:8080/api";
    private static final String ACCOUNT_SERVICE = "http://localhost:8083/api";
    private static final String TRANSFER_SERVICE = "http://localhost:8084/api";

    public GatewayResource() {
        this.client = ClientBuilder.newClient();
    }

    @POST
    @Path("/auth/{path: .*}")
    public Response proxyAuth(@PathParam("path") String path, InputStream body, @Context HttpHeaders headers) {
        return forwardRequest(AUTH_SERVICE + "/auth/" + path, "POST", body, headers);
    }

    @GET
    @Path("/auth/{path: .*}")
    public Response proxyAuthGet(@PathParam("path") String path, @Context HttpHeaders headers) {
        return forwardRequest(AUTH_SERVICE + "/auth/" + path, "GET", null, headers);
    }

    @POST
    @Path("/transfers/{path: .*}")
    public Response proxyTransfer(@PathParam("path") String path, InputStream body, @Context HttpHeaders headers) {
        return forwardRequest(TRANSFER_SERVICE + "/transfers/" + path, "POST", body, headers);
    }

    // ==========================================
    // ACCOUNT SERVICE ROUTING (Supports CRUD)
    // ==========================================

    @GET
    @Path("/accounts/{path: .*}")
    public Response getAccount(@PathParam("path") String path, @Context HttpHeaders headers) {
        return forwardRequest(ACCOUNT_SERVICE + "/accounts/" + path, "GET", null, headers);
    }

    @POST
    @Path("/accounts/{path: .*}")
    public Response createAccount(@PathParam("path") String path, InputStream body, @Context HttpHeaders headers) {
        return forwardRequest(ACCOUNT_SERVICE + "/accounts/" + path, "POST", body, headers);
    }

    @PUT
    @Path("/accounts/{path: .*}")
    public Response updateAccount(@PathParam("path") String path, InputStream body, @Context HttpHeaders headers) {
        return forwardRequest(ACCOUNT_SERVICE + "/accounts/" + path, "PUT", body, headers);
    }

    @DELETE
    @Path("/accounts/{path: .*}")
    public Response deleteAccount(@PathParam("path") String path, @Context HttpHeaders headers) {
        return forwardRequest(ACCOUNT_SERVICE + "/accounts/" + path, "DELETE", null, headers);
    }

    // ==========================================
    // GENERIC FORWARDER (The "Dumb Pipe")
    // ==========================================

    private Response forwardRequest(String targetUrl, String method, InputStream body, HttpHeaders headers) {
        var requestBuilder = client.target(targetUrl).request();

        // 1. Forward Authorization Header (Pass the JWT)
        String authHeader = headers.getHeaderString(HttpHeaders.AUTHORIZATION);
        if (authHeader != null) {
            requestBuilder.header(HttpHeaders.AUTHORIZATION, authHeader);
        }

        // 2. Execute based on Method
        Response backendResponse;

        // We use InputStream 'body' directly to stream data without parsing it (Performance + Simplicity)
        Entity<InputStream> entity = (body != null) ? Entity.entity(body, MediaType.APPLICATION_JSON) : null;

        switch (method) {
            case "POST":
                backendResponse = requestBuilder.post(entity);
                break;
            case "PUT":
                backendResponse = requestBuilder.put(entity);
                break;
            case "DELETE":
                backendResponse = requestBuilder.delete();
                break;
            case "GET":
            default:
                backendResponse = requestBuilder.get();
                break;
        }

        // 3. Return the exact response from the microservice
        return Response.status(backendResponse.getStatus())
                .entity(backendResponse.readEntity(InputStream.class)) // Stream output back
                .type(backendResponse.getMediaType())
                .build();
    }
}