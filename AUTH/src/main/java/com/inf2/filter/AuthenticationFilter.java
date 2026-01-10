package com.inf2.filter;
import io.smallrye.jwt.auth.principal.JWTAuthContextInfo;
import io.smallrye.jwt.auth.principal.JWTCallerPrincipal;
import io.smallrye.jwt.auth.principal.JWTCallerPrincipalFactory;
import io.smallrye.jwt.auth.principal.ParseException;
import org.eclipse.microprofile.jwt.JsonWebToken;

import jakarta.annotation.Priority;
import jakarta.inject.Inject;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import jakarta.ws.rs.ext.Provider;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.Principal;

@Secured // Bind to methods annotated with @Secured
@Provider // Register as a JAX-RS component
@Priority(Priorities.AUTHENTICATION) // Run this first!
public class AuthenticationFilter implements ContainerRequestFilter {


    @Override
    public void filter(ContainerRequestContext requestContext) {
        // 1. Get the HTTP Authorization header from the request
        String authorizationHeader = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);

        // 2. Check if the header is present and formatted correctly
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            abortWithUnauthorized(requestContext);
            return;
        }

        // 3. Extract the token
        String token = authorizationHeader.substring("Bearer ".length()).trim();

        try {
            // 1. Configure the Context (Tell it where the key is and who issued it)
            JWTAuthContextInfo contextInfo = new JWTAuthContextInfo();

            // Ensure this path matches where you put the file in src/main/resources
            String publicKeyContent = readPublicKeyContent("/publicKey.pem");
            contextInfo.setPublicKeyContent(publicKeyContent);

            contextInfo.setIssuedBy("https://ta-banque.com");

            JWTCallerPrincipalFactory factory = JWTCallerPrincipalFactory.instance();
            JWTCallerPrincipal principal = factory.parse(token, contextInfo);

            // 4. Set the Security Context
            final SecurityContext currentSecurityContext = requestContext.getSecurityContext();
            requestContext.setSecurityContext(new SecurityContext() {
                @Override
                public Principal getUserPrincipal() {
                    return principal;
                }

                @Override
                public boolean isUserInRole(String role) {
                    return principal.getGroups().contains(role);
                }

                @Override
                public boolean isSecure() { return currentSecurityContext.isSecure(); }

                @Override
                public String getAuthenticationScheme() { return "Bearer"; }
            });

        } catch (ParseException e) {
            System.err.println("JWT Parse Error: " + e.getMessage());
            e.printStackTrace();

            requestContext.abortWith(
                    Response.status(Response.Status.UNAUTHORIZED)
                            .entity("Invalid Token: " + e.getMessage()) // Return message for debugging
                            .build()
            );
        } catch (Exception e) {
            e.printStackTrace(); // Log the real error
            requestContext.abortWith(Response.serverError().build());
        }
    }

    private void abortWithUnauthorized(ContainerRequestContext requestContext) {
        requestContext.abortWith(
                Response.status(Response.Status.UNAUTHORIZED)
                        .entity("Invalid or missing access token")
                        .build());
    }
    private String readPublicKeyContent(String path) throws Exception {
        InputStream is = getClass().getResourceAsStream(path);
        if (is == null) throw new RuntimeException("PublicKey not found at: " + path);
        return new String(is.readAllBytes(), StandardCharsets.UTF_8);
    }
}