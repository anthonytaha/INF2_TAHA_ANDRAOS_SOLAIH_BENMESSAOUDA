package com.inf2.resource;

import com.inf2.service.HelloService;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Response;

@Path("hello")
public class HelloResource {

    // Inject a Provider for the Service, ensuring it's fetched when needed.
    @Inject
    private HelloService helloServiceProvider;

    @GET
    public Response greet(@QueryParam("name") String name) {

        try {
            // Retrieve the service instance via the Provider
            HelloService helloService = helloServiceProvider;

            String greeting = helloService.saveGreeting(name);

            return Response.ok(greeting).build();

        } catch (RuntimeException e) {
            // ... (error handling remains the same)
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Application Error: " + e.getMessage()).build();
        }
    }
}