package com.inf2.resource;

import com.inf2.domain.Account;
import com.inf2.dto.AccountCreateRequest;
import com.inf2.dto.AccountUpdateRequest;
import com.inf2.dto.BalanceUpdateRequest;
import com.inf2.filter.Secured;
import com.inf2.service.AccountService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;

import java.util.List;
import java.util.UUID;

@Path("/accounts")
public class AccountResource {

    @Inject
    private AccountService accountService;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Secured
    @RolesAllowed({"advisor"})
    public Response createAccount(AccountCreateRequest request) {
        try {
            Account created = accountService.createAccount(request);
            return Response.status(Response.Status.CREATED).entity(created).build();
        } catch (RuntimeException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Secured
    @RolesAllowed({"advisor","client"})
    public Response getAccounts(@QueryParam("clientId") UUID clientId, @Context SecurityContext securityContext) {
        try {
            String callerId = securityContext.getUserPrincipal().getName(); // user_id from Token
            boolean isAdvisor = securityContext.isUserInRole("advisor");
            if (isAdvisor && (clientId == null || clientId.toString().isEmpty())) {
                return Response.status(Response.Status.BAD_REQUEST).entity("clientId is required").build();
            }
            if (!isAdvisor) {
                clientId = UUID.fromString(callerId);
            }
            List<Account> accounts = accountService.findByClientId(clientId);
            return Response.ok(accounts).build();
        } catch (RuntimeException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Secured
    @RolesAllowed({"advisor", "client"})
    public Response getAccountById(
            @PathParam("id") UUID id,
            @Context SecurityContext securityContext
    ) {
        try {
            String callerId = securityContext.getUserPrincipal().getName();
            boolean isAdvisor = securityContext.isUserInRole("advisor");

            var account = accountService.findById(id);

            if (account == null) {
                return Response.status(Response.Status.NOT_FOUND).build();
            }

            if (!isAdvisor) {
                if (!account.getClientId().equals(callerId)) {
                    return Response.status(Response.Status.FORBIDDEN)
                            .entity("You do not have access to this account")
                            .build();
                }
            }

            return Response.ok(account).build();

        } catch (RuntimeException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(e.getMessage())
                    .build();
        }
    }
    // detail change (Update Type/Status)
    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Secured
    @RolesAllowed({"advisor"})
    public Response updateAccount(@PathParam("id") UUID id, AccountUpdateRequest request) {
        try {
            accountService.updateAccount(id, request);
            return Response.ok().build();
        } catch (RuntimeException e) {
            return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
        }
    }
}