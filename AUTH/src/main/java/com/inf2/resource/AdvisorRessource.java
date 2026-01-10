package com.inf2.resource;

import com.inf2.domain.Advisor;
import com.inf2.dto.advisor.AdvisorCreateRequest;
import com.inf2.dto.auth.UserUpdateRequest;
import com.inf2.service.domain.AdvisorService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.UUID;

@Path("advisor")
public class AdvisorRessource {
    @Inject
    private AdvisorService advisorService;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createAdvisor(AdvisorCreateRequest advisor) {
        try {
            advisorService.createAdvisor(advisor);
            return Response.status(Response.Status.CREATED).build();
        } catch (RuntimeException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAdvisor(@PathParam("id") UUID id) {
        try {
            Advisor fetchedAdvisor = advisorService.getAdvisorById(id);
            if (fetchedAdvisor == null) {
                return Response.status(Response.Status.NOT_FOUND).build();
            }
            return Response.status(Response.Status.OK).entity(fetchedAdvisor).build();
        }catch (RuntimeException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }

    @DELETE
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteAdvisor(@PathParam("id") UUID id) {
        try {
            advisorService.deleteAdvisor(id);
            return Response.status(Response.Status.OK).build();
        }catch (RuntimeException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }

    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateAdvisor(@PathParam("id") UUID id, UserUpdateRequest userUpdateRequest) {
        try {
            advisorService.updateAdvisor(id, userUpdateRequest);
            return Response.status(Response.Status.OK).build();
        }catch (RuntimeException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }
}
