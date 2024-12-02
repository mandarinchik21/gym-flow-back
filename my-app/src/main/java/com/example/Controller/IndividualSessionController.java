package com.example.Controller;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import com.example.DAO.IndividualSessionDAO;
import com.example.DAO.SessionService;
import com.example.Model.IndividualSession;
import com.example.Model.SignUpRequest;
import com.example.Model.TrainerSchedule;
import com.example.DAO.ClientDAO;
import com.example.Model.DeleteSessionRequest;

@Path("/individual-sessions")
public class IndividualSessionController {

    private IndividualSessionDAO individualSessionDAO;
    private final SessionService sessionService;

    public IndividualSessionController() {
        this.individualSessionDAO = new IndividualSessionDAO();
        this.sessionService = new SessionService();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createIndividualSession(IndividualSession session) {
        try {
            individualSessionDAO.createSession(session);
            return Response.status(Response.Status.CREATED).entity(session).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllIndividualSessions() {
        try {
            List<IndividualSession> sessions = individualSessionDAO.getAllSessions();
            return Response.ok(sessions).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getIndividualSessionById(@PathParam("id") int id) {
        try {
            IndividualSession session = individualSessionDAO.getSessionById(id);
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
    public Response updateIndividualSession(@PathParam("id") int id, IndividualSession session) {
        try {
            session.setId(id);
            boolean updated = individualSessionDAO.updateSession(session);
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
    public Response deleteIndividualSession(@PathParam("id") int id) {
        try {
            boolean deleted = individualSessionDAO.deleteSession(id);
            if (!deleted) {
                return Response.status(Response.Status.NOT_FOUND).build();
            }
            return Response.status(Response.Status.NO_CONTENT).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }

    @GET
    @Path("/available/{clientId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAvailableSessionsForClient(@PathParam("clientId") int clientId) {
        try {
            List<TrainerSchedule> trainerSchedules = sessionService.getAvailableTrainerSchedules(clientId);

            return Response.ok(trainerSchedules).build();
        } catch (SQLException e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Database error").build();
        }
    }

    @POST
    @Path("/signup")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response signUpForIndividualSession(SignUpRequest signUpRequest) {
        try {
            ClientDAO clientDAO = new ClientDAO();

            int clientId = clientDAO.getClientIdByEmail(signUpRequest.getEmail());
            if (clientId == 0) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("Client with the given email does not exist.")
                        .build();
            }

            boolean hasMembership = clientDAO.hasActiveMembership(clientId);
            if (!hasMembership) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("Client does not have an active membership.")
                        .build();
            }

            boolean isAlreadySignedUp = sessionService.isClientAlreadySignedUp(
                    clientId,
                    signUpRequest.getTrainerId(),
                    LocalDate.parse(signUpRequest.getDate()),
                    LocalTime.parse(signUpRequest.getTime())
            );

            if (isAlreadySignedUp) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("Client is already signed up for this session.")
                        .build();
            }

            boolean success = sessionService.signUpForIndividualSession(
                    clientId,
                    signUpRequest.getTrainerId(),
                    LocalDate.parse(signUpRequest.getDate()),
                    LocalTime.parse(signUpRequest.getTime())
            );

            if (success) {
                boolean sessionRemoved = sessionService.removeSessionFromTrainerSchedule(
                        signUpRequest.getTrainerId(),
                        LocalDate.parse(signUpRequest.getDate()),
                        LocalTime.parse(signUpRequest.getTime())
                );

                if (!sessionRemoved) {
                    return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                            .entity("Failed to remove session from trainer schedule.")
                            .build();
                }

                return Response.ok("Successfully signed up for the session").build();
            } else {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                        .entity("Failed to sign up for the session")
                        .build();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Database error occurred while processing the request.")
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("An error occurred while processing the request.")
                    .build();
        }
    }


    @DELETE
    @Path("/cancel")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response cancelSession(DeleteSessionRequest deleteSessionRequest) {
        try {
            ClientDAO clientDAO = new ClientDAO();
            // Отримуємо ID користувача за його електронною поштою
            int clientId = clientDAO.getClientIdByEmail(deleteSessionRequest.getEmail());
            if (clientId == 0) {
                return Response.status(Response.Status.NOT_FOUND)
                            .entity("User not found with email: " + deleteSessionRequest.getEmail())
                            .build();
            }

            int sessionId = sessionService.getSessionId(
                clientId,
                deleteSessionRequest.getTrainerId(),
                LocalDate.parse(deleteSessionRequest.getDate()), 
                LocalTime.parse(deleteSessionRequest.getTime())   
            );

            if (sessionId == -1) {
                return Response.status(Response.Status.NOT_FOUND)
                            .entity("No session found for the provided details.")
                            .build();
            }

            boolean success = sessionService.deleteSession(sessionId);

            if (success) {
                return Response.ok("Successfully canceled the session").build();
            } else {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                            .entity("Failed to cancel the session")
                            .build();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                        .entity("An error occurred while processing your request")
                        .build();
        }
    }
}
