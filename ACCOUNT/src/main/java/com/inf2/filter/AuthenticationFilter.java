package com.inf2.filter;

import io.smallrye.jwt.auth.principal.JWTAuthContextInfo;
import io.smallrye.jwt.auth.principal.JWTCallerPrincipal;
import io.smallrye.jwt.auth.principal.JWTCallerPrincipalFactory;
import io.smallrye.jwt.auth.principal.ParseException;
import jakarta.annotation.Priority;
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

@Secured
@Provider
@Priority(Priorities.AUTHENTICATION)
public class AuthenticationFilter implements ContainerRequestFilter {


    @Override
    public void filter(ContainerRequestContext requestContext) {
        String authorizationHeader = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);

        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            abortWithUnauthorized(requestContext);
            return;
        }

        String token = authorizationHeader.substring("Bearer ".length()).trim();

        try {
            JWTAuthContextInfo contextInfo = new JWTAuthContextInfo();

            String publicKeyContent = readPublicKeyContent("/publicKey.pem");
            contextInfo.setPublicKeyContent(publicKeyContent);

            contextInfo.setIssuedBy("https://ta-banque.com");

            JWTCallerPrincipalFactory factory = JWTCallerPrincipalFactory.instance();
            JWTCallerPrincipal principal = factory.parse(token, contextInfo);

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
            e.printStackTrace();
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