package com.inf2.resource;

import com.inf2.filter.Secured;
import com.inf2.service.HelloService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Response;

@Path("hello")
public class HelloResource {

    @Inject
    private HelloService helloService;

    @GET
    @Secured
    @RolesAllowed({"advisor"})
    public Response greet(@QueryParam("name") String name) {

        try {
            String greeting = helloService.saveGreeting(name);

            return Response.ok(greeting).build();

        } catch (RuntimeException e) {
            // ... (error handling remains the same)
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Application Error: " + e.getMessage()).build();
        }
    }
}