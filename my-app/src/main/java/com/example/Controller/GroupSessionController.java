package com.example.Controller;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import com.example.DatabaseConnection;
import java.sql.ResultSet;
import java.sql.Connection;
import com.example.DAO.GroupSessionDAO;
import com.example.DAO.ClientDAO;
import com.example.Model.GroupSession;
import com.example.Model.SessionParticipant;
import com.example.Model.SignUpRequest;
import com.example.DAO.SessionService;

@Path("/group-sessions")
public class GroupSessionController {

    private GroupSessionDAO groupSessionDAO;
    private SessionService sessionService;
    private ClientDAO clientDAO;
    
    public GroupSessionController() {
        this.groupSessionDAO = new GroupSessionDAO();
        this.sessionService = new SessionService();
        this.clientDAO = new ClientDAO();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createGroupSession(GroupSession session) {
        try {
            groupSessionDAO.createSession(session);
            return Response.status(Response.Status.CREATED).entity(session).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllGroupSessions() {
        try {
            List<GroupSession> sessions = groupSessionDAO.getAllSessions();
            return Response.ok(sessions).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getGroupSessionById(@PathParam("id") int id) {
        try {
            GroupSession session = groupSessionDAO.getSessionById(id);
            if (session == null) {
                return Response.status(Response.Status.NOT_FOUND).build();
            }
            return Response.ok(session).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }

    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateGroupSession(@PathParam("id") int id, GroupSession session) {
        try {
            session.setId(id);
            boolean updated = groupSessionDAO.updateSession(session);
            if (!updated) {
                return Response.status(Response.Status.NOT_FOUND).build();
            }
            return Response.ok(session).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }

    @DELETE
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteGroupSession(@PathParam("id") int id) {
        try {
            boolean deleted = groupSessionDAO.deleteSession(id);
            if (!deleted) {
                return Response.status(Response.Status.NOT_FOUND).build();
            }
            return Response.status(Response.Status.NO_CONTENT).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }

    @POST
    @Path("/signup")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response signUpForGroupSession(SignUpRequest signUpRequest) {
        try {
            // Отримуємо clientId за email
            int clientId = clientDAO.getClientIdByEmail(signUpRequest.getEmail());
            if (clientId == 0) { 
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("Клієнта з таким email не знайдено.")
                        .build();
            }

            String subscriptionType = sessionService.getSubscriptionTypeForClient(clientId);
            if (!(subscriptionType.contains("Group") || subscriptionType.contains("All Training"))) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("Ваша підписка не включає групові тренування.")
                        .build();
            }

            int sessionId = getSessionIdByDateTime(signUpRequest.getDate(), signUpRequest.getTime(), signUpRequest.getTrainerId());
            if (sessionId == 0) { // Якщо сесію не знайдено
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("Не знайдено сесії для зазначених дати, часу та тренера.")
                        .build();
            }

            boolean isAlreadySignedUp = sessionService.isClientAlreadySignedUp(clientId, sessionId);
            if (isAlreadySignedUp) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("Клієнт вже записаний на цю сесію.")
                        .build();
            }

            boolean success = sessionService.signUpForGroupSession(clientId, sessionId);
            if (success) {
                return Response.status(Response.Status.CREATED)
                        .entity("Успішно зареєстровано на групову сесію.")
                        .build();
            } else {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                        .entity("Не вдалося зареєструвати клієнта на сесію.")
                        .build();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Помилка при обробці реєстрації: " + e.getMessage())
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Сталася помилка при обробці запиту.")
                    .build();
        }
    }

    private int getSessionIdByDateTime(String date, String time, int trainerId) throws SQLException {
        String query = "SELECT id FROM group_sessions WHERE date = ?::DATE AND time = ?::TIME AND trainer_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, date); // дата у форматі "YYYY-MM-DD"
            stmt.setString(2, time); // час у форматі "HH:MM:SS"
            stmt.setInt(3, trainerId);
            
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt("id");
            } else {
                return 0; // Якщо сесія не знайдена
            }
        }
    }

    @GET
    @Path("/available/{clientId}")
    @Produces(MediaType.APPLICATION_JSON)
    public List<GroupSession> getAvailableGroupSessions(@PathParam("clientId") int clientId) {
        try {
            return sessionService.getAvailableGroupSessions(clientId);
        } catch (SQLException e) {
            throw new WebApplicationException("Error retrieving available group sessions: " + e.getMessage(), 500);
        }
    }

    @DELETE
    @Path("/cancel")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public boolean cancelGroupSession(SessionParticipant cancelRequest) {
        try {
            return sessionService.cancelGroupSession(cancelRequest.getParticipantId(), cancelRequest.getSessionId());
        } catch (SQLException e) {
            throw new WebApplicationException("Error canceling group session: " + e.getMessage(), 500);
        }
    }
}

