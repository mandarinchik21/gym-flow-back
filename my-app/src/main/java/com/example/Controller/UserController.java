package com.example.Controller;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;
import org.mindrot.jbcrypt.BCrypt;

import com.example.DAO.UserDAO;
import com.example.Model.User;

@Path("/users")
public class UserController {

    private UserDAO userDAO = new UserDAO();

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createUser(User user) {
        try {
            validateUser(user);

            user.setPassword(hashPassword(user.getPassword()));

            if (isCorporateEmail(user.getEmail())) {
                if (user.getAdminKey() == null || !isValidAdminKey(user.getAdminKey())) {
                    return Response.status(Response.Status.BAD_REQUEST)
                            .entity("Для корпоративної пошти потрібно вказати валідний adminKey.")
                            .build();
                }
                user.setRole("admin");
            } else {
                user.setRole("user");
            }

            userDAO.createUser(user);
            user.setPassword(null);
            return Response.status(Response.Status.CREATED).entity(user).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }

    private void validateUser(User user) {
        if (user.getName() == null || user.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Ім'я є обов'язковим полем.");
        }
        if (user.getSurname() == null || user.getSurname().trim().isEmpty()) {
            throw new IllegalArgumentException("Прізвище є обов'язковим полем.");
        }
        if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("Email є обов'язковим полем.");
        }
        if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
            throw new IllegalArgumentException("Пароль є обов'язковим полем.");
        }
    }

    private boolean isCorporateEmail(String email) {
        return email != null && email.endsWith("@company.com");
    }

    private boolean isValidAdminKey(String adminKey) {
        return "admin-key-123".equals(adminKey);
    }

    private String hashPassword(String plainPassword) {
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt());
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllUsers() {
        try {
            List<User> users = userDAO.getAllUsers();

            users.forEach(user -> user.setPassword(null));

            return Response.ok(users).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUserById(@PathParam("id") int id) {
        try {
            User user = userDAO.getUserById(id);
            if (user == null) {
                return Response.status(Response.Status.NOT_FOUND).build();
            }

            user.setPassword(null); 
            return Response.ok(user).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }

    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateUser(@PathParam("id") int id, User user) {
        try {
            user.setId(id);
            boolean updated = userDAO.updateUser(user);
            if (!updated) {
                return Response.status(Response.Status.NOT_FOUND).build();
            }

            user.setPassword(null); 
            return Response.ok(user).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }

    @DELETE
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteUser(@PathParam("id") int id) {
        try {
            boolean deleted = userDAO.deleteUser(id);
            if (!deleted) {
                return Response.status(Response.Status.NOT_FOUND).build();
            }
            return Response.status(Response.Status.NO_CONTENT).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }
}
