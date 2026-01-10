package com.inf2.resource;


import com.inf2.domain.TransferRecipient;
import com.inf2.dto.transfer.RecipientCreateRequest;
import com.inf2.dto.transfer.RecipientValidationRequest;
import com.inf2.filter.Secured;
import com.inf2.service.RecipientService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;

import java.util.List;
import java.util.UUID;

@Path("/recipients")
public class RecipientResource {

    @Inject
    private RecipientService recipientService;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Secured
    @RolesAllowed({"client"})
    public Response registerRecipient(RecipientCreateRequest request,
                                      @Context SecurityContext securityContext) {
        try {
            String clientId = securityContext.getUserPrincipal().getName();
            if (clientId == null || clientId.isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("clientId est requis").build();
            }

            TransferRecipient recipient = recipientService.registerRecipient(request, UUID.fromString(clientId));
            return Response.status(Response.Status.CREATED).entity(recipient).build();

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
    public Response getRecipientById(@PathParam("id") UUID id) {
        try {
            TransferRecipient recipient = recipientService.findById(id);
            if (recipient != null) {
                return Response.ok(recipient).build();
            } else {
                return Response.status(Response.Status.NOT_FOUND).build();
            }
        } catch (RuntimeException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(e.getMessage()).build();
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Secured
    @RolesAllowed({"advisor"})
    public Response getRecipients(@Context SecurityContext securityContext,
                                  @QueryParam("validated") Boolean validated) {
        try {
            String clientId = securityContext.getUserPrincipal().getName();
            if (clientId == null || clientId.isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("clientId est requis").build();
            }

            List<TransferRecipient> recipients;

            if (validated != null && validated) {
                recipients = recipientService.findValidatedByClientId(UUID.fromString(clientId));
            } else {
                recipients = recipientService.findByClientId(UUID.fromString(clientId));
            }

            return Response.ok(recipients).build();

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
    public Response getPendingRecipients() {
        try {
            List<TransferRecipient> recipients = recipientService.findPendingRecipients();
            return Response.ok(recipients).build();
        } catch (RuntimeException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(e.getMessage()).build();
        }
    }

    @PUT
    @Path("/{id}/validate")
    @Consumes(MediaType.APPLICATION_JSON)
    @Secured
    @RolesAllowed({"advisor"})
    public Response validateRecipient(@PathParam("id") UUID recipientId,
                                      RecipientValidationRequest request) {
        try {
            if (request.getAdvisorId() == null || request.getAdvisorId().toString().isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("advisorId est requis").build();
            }

            recipientService.validateRecipient(recipientId, request.getAdvisorId());
            return Response.ok().build();

        } catch (RuntimeException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(e.getMessage()).build();
        }
    }

    @PUT
    @Path("/{id}/reject")
    @Consumes(MediaType.APPLICATION_JSON)
    @Secured
    @RolesAllowed({"advisor"})
    public Response rejectRecipient(@PathParam("id") UUID recipientId,
                                    RecipientValidationRequest request) {
        try {
            if (request.getAdvisorId() == null || request.getAdvisorId().toString().isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("advisorId est requis").build();
            }

            recipientService.rejectRecipient(recipientId, request.getAdvisorId());
            return Response.ok().build();

        } catch (RuntimeException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(e.getMessage()).build();
        }
    }

    @DELETE
    @Path("/{id}")
    @Secured
    @RolesAllowed({"client"})
    public Response deleteRecipient(@PathParam("id") UUID id,
                                    @Context SecurityContext securityContext) {
        try {
            String clientId = securityContext.getUserPrincipal().getName();

            if (clientId == null || clientId.isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("clientId est requis").build();
            }

            recipientService.deleteRecipient(id, UUID.fromString(clientId));
            return Response.noContent().build();

        } catch (RuntimeException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(e.getMessage()).build();
        }
    }
}
