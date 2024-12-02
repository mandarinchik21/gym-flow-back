package com.example.Controller;

import com.example.DAO.AuthService;
import com.example.Model.Credentials;
import com.example.Model.User;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/auth")
public class AuthController {

    private AuthService authService = new AuthService();

    @POST
    @Path("/register")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response register(User user) {
        try {
            if (user == null || 
                user.getEmail() == null || user.getEmail().isEmpty() ||
                user.getPassword() == null || user.getPassword().isEmpty() ||
                user.getSurname() == null || user.getSurname().isEmpty() ||
                user.getName() == null || user.getName().isEmpty()) {
                throw new Exception("Усі обов'язкові поля (email, password, surname, name) повинні бути заповнені");
            }

            if (user.getEmail().endsWith("@company.com") && 
                (user.getAdminKey() == null || user.getAdminKey().isEmpty())) {
                throw new Exception("Для корпоративної пошти необхідно вказати adminKey");
            }

            authService.register(user);
            return Response.status(Response.Status.CREATED).entity("User registered successfully").build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }

    @POST
    @Path("/login")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response login(Credentials credentials) {
        try {
            if (credentials == null || 
                credentials.getEmail() == null || credentials.getEmail().isEmpty() ||
                credentials.getPassword() == null || credentials.getPassword().isEmpty()) {
                throw new Exception("Всі обов'язкові поля повинні бути заповнені (email, password)");
            }

            String token = authService.login(
                credentials.getEmail(), 
                credentials.getPassword(), 
                credentials.getAdminKey()
            );

            return Response.ok().entity(token).build();
        } catch (Exception e) {
            return Response.status(Response.Status.UNAUTHORIZED)
                        .entity(e.getMessage())
                        .build();
        }
    }
}
