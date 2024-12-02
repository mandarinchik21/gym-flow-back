package com.example.Controller;

import com.example.DAO.SessionService;
import com.example.Model.SessionDetails;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.sql.SQLException;
import java.util.List;

@Path("/sessions")
public class SessionController {

    private SessionService sessionService = new SessionService();

    @GET
    @Path("/{clientId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSessionsForClient(@PathParam("clientId") int clientId) {
        try {
            List<SessionDetails> sessions = sessionService.getAllSessionsForClient(clientId);
            return Response.ok(sessions).build();
        } catch (SQLException e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Database error").build();
        }
    }
}