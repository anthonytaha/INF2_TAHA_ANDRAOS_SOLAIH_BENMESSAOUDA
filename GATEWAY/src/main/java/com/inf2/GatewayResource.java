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

    @GET
    @Path("/auth{path: .*}")
    public Response proxyAuthGet(@PathParam("path") String path, @Context HttpHeaders headers) {
        return forwardRequest(AUTH_SERVICE + "/auth" + path, "GET", null, headers);
    }

    @POST
    @Path("/auth{path: .*}")
    public Response proxyAuth(@PathParam("path") String path, InputStream body, @Context HttpHeaders headers) {
        return forwardRequest(AUTH_SERVICE + "/auth" + path, "POST", body, headers);
    }

    @GET
    @Path("/advisor{path: .*}")
    public Response proxyAdvisorGet(@PathParam("path") String path, @Context HttpHeaders headers) {
        return forwardRequest(AUTH_SERVICE + "/advisor" + path, "GET", null, headers);
    }

    @POST
    @Path("/advisor{path: .*}")
    public Response proxyAdvisor(@PathParam("path") String path, InputStream body, @Context HttpHeaders headers) {
        return forwardRequest(AUTH_SERVICE + "/advisor" + path, "POST", body, headers);
    }

    @GET
    @Path("/client{path: .*}")
    public Response proxyClientGet(@PathParam("path") String path, @Context HttpHeaders headers) {
        return forwardRequest(AUTH_SERVICE + "/client" + path, "GET", null, headers);
    }

    @POST
    @Path("/client{path: .*}")
    public Response proxyClient(@PathParam("path") String path, InputStream body, @Context HttpHeaders headers) {
        return forwardRequest(AUTH_SERVICE + "/client" + path, "POST", body, headers);
    }

    @GET
    @Path("/transfers{path: .*}")
    public Response proxyTransferGet(@PathParam("path") String path, InputStream body, @Context HttpHeaders headers) {
        return forwardRequest(TRANSFER_SERVICE + "/transfers" + path, "GET", null, headers);
    }
    @POST
    @Path("/transfers{path: .*}")
    public Response proxyTransfer(@PathParam("path") String path, InputStream body, @Context HttpHeaders headers) {
        return forwardRequest(TRANSFER_SERVICE + "/transfers" + path, "POST", body, headers);
    }

    @GET
    @Path("/recipients{path: .*}")
    public Response proxyRecipientGet(@PathParam("path") String path, InputStream body, @Context HttpHeaders headers) {
        return forwardRequest(TRANSFER_SERVICE + "/recipients" + path, "GET", null, headers);
    }
    @POST
    @Path("/recipients{path: .*}")
    public Response proxyRecipient(@PathParam("path") String path, InputStream body, @Context HttpHeaders headers) {
        return forwardRequest(TRANSFER_SERVICE + "/recipients" + path, "POST", body, headers);
    }
    @PUT
    @Path("/recipients{path: .*}")
    public Response proxyRecipientPut(@PathParam("path") String path, InputStream body, @Context HttpHeaders headers) {
        return forwardRequest(TRANSFER_SERVICE + "/recipients" + path, "PUT", body, headers);
    }

    @GET
    @Path("/accounts{path: .*}")
    public Response getAccount(@PathParam("path") String path, @Context HttpHeaders headers) {
        return forwardRequest(ACCOUNT_SERVICE + "/accounts" + path, "GET", null, headers);
    }

    @POST
    @Path("/accounts{path: .*}")
    public Response createAccount(@PathParam("path") String path, InputStream body, @Context HttpHeaders headers) {
        return forwardRequest(ACCOUNT_SERVICE + "/accounts" + path, "POST", body, headers);
    }

    @PUT
    @Path("/accounts{path: .*}")
    public Response updateAccount(@PathParam("path") String path, InputStream body, @Context HttpHeaders headers) {
        return forwardRequest(ACCOUNT_SERVICE + "/accounts" + path, "PUT", body, headers);
    }

    @DELETE
    @Path("/accounts{path: .*}")
    public Response deleteAccount(@PathParam("path") String path, @Context HttpHeaders headers) {
        return forwardRequest(ACCOUNT_SERVICE + "/accounts" + path, "DELETE", null, headers);
    }

    private Response forwardRequest(String targetUrl, String method, InputStream body, HttpHeaders headers) {
        var requestBuilder = client.target(targetUrl).request();

        String authHeader = headers.getHeaderString(HttpHeaders.AUTHORIZATION);
        if (authHeader != null) {
            requestBuilder.header(HttpHeaders.AUTHORIZATION, authHeader);
        }

        Response backendResponse;

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

        return Response.status(backendResponse.getStatus())
                .entity(backendResponse.readEntity(InputStream.class)) // Stream output back
                .type(backendResponse.getMediaType())
                .build();
    }
}