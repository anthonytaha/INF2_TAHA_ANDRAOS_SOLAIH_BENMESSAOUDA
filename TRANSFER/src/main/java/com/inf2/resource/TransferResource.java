package com.inf2.resource;


import com.inf2.domain.Transfer;
import com.inf2.dto.TransferCreateRequest;
import com.inf2.filter.Secured;
import com.inf2.service.TransferService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;

import java.util.List;
import java.util.UUID;

@Path("/transfers")
public class TransferResource {

    @Inject
    private TransferService transferService;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Secured
    @RolesAllowed({"client"})
    public Response createTransfer(TransferCreateRequest request, @Context SecurityContext securityContext) {
        try {
            String clientId = securityContext.getUserPrincipal().getName();
            if (clientId == null || clientId.isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("clientId est requis").build();
            }

            Transfer transfer = transferService.createTransfer(request, UUID.fromString(clientId));
            return Response.status(Response.Status.ACCEPTED).entity(transfer).build();

        } catch (RuntimeException e) {
            e.printStackTrace();
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(e.getMessage()).build();
        }
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Secured
    @RolesAllowed({"advisor"})
    public Response getTransferById(@PathParam("id") UUID id) {
        try {
            Transfer transfer = transferService.findById(id);
            if (transfer != null) {
                return Response.ok(transfer).build();
            } else {
                return Response.status(Response.Status.NOT_FOUND).build();
            }
        } catch (RuntimeException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(e.getMessage()).build();
        }
    }

    @GET
    @Path("/correlation/{correlationId}")
    @Produces(MediaType.APPLICATION_JSON)
    @Secured
    @RolesAllowed({"advisor"})
    public Response getTransferByCorrelationId(@PathParam("correlationId") UUID correlationId) {
        try {
            Transfer transfer = transferService.findByCorrelationId(correlationId);
            if (transfer != null) {
                return Response.ok(transfer).build();
            } else {
                return Response.status(Response.Status.NOT_FOUND).build();
            }
        } catch (RuntimeException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(e.getMessage()).build();
        }
    }

    @GET
    @Path("/account/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Secured
    @RolesAllowed({"advisor"})
    public Response getTransfers(@PathParam("id") UUID accountId,
                                 @QueryParam("type") String type) {
        try {
            if (accountId == null || accountId.toString().isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("accountId est requis").build();
            }

            List<Transfer> transfers;

            if ("sent".equals(type)) {
                transfers = transferService.findSentTransfers(accountId);
            } else if ("received".equals(type)) {
                transfers = transferService.findReceivedTransfers(accountId);
            } else {
                transfers = transferService.findByAccountId(accountId);
            }

            return Response.ok(transfers).build();

        } catch (RuntimeException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(e.getMessage()).build();
        }
    }

    @GET
    @Path("/pending")
    @Produces(MediaType.APPLICATION_JSON)
    @Secured
    @RolesAllowed({"advisor"})
    public Response getPendingTransfers() {
        try {
            List<Transfer> transfers = transferService.findPendingTransfers();
            return Response.ok(transfers).build();
        } catch (RuntimeException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(e.getMessage()).build();
        }
    }

    @DELETE
    @Path("/{id}")
    @Secured
    @RolesAllowed({"advisor"})
    public Response cancelTransfer(@PathParam("id") UUID id) {
        try {
            transferService.cancelTransfer(id);
            return Response.noContent().build();
        } catch (RuntimeException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(e.getMessage()).build();
        }
    }
}
