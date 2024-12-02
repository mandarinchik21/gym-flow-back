package com.example.Controller;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

import com.example.DAO.SessionParticipantDAO;
import com.example.Model.SessionParticipant;

@Path("/session-participants")
public class SessionParticipantController {

    private SessionParticipantDAO participantDAO;

    public SessionParticipantController() {
        this.participantDAO = new SessionParticipantDAO();
    }
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllParticipants() {
        try {
            List<SessionParticipant> participants = participantDAO.getAllParticipants();
            if (participants.isEmpty()) {
                return Response.status(Response.Status.NOT_FOUND).entity("Немає учасників").build();
            }
            return Response.ok(participants).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }

    @GET
    @Path("/{participantId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getParticipantById(@PathParam("participantId") int participantId) {
        try {
            List<SessionParticipant> participants = participantDAO.getParticipantsByParticipantId(participantId);
            if (participants.isEmpty()) {
                return Response.status(Response.Status.NOT_FOUND).entity("Учасника не знайдено").build();
            }
            return Response.ok(participants).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addParticipant(SessionParticipant participant) {
        try {
            boolean added = participantDAO.addParticipant(participant);
            if (added) {
                return Response.status(Response.Status.CREATED).entity(participant).build();
            } else {
                return Response.status(Response.Status.BAD_REQUEST).build();
            }
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }

    @DELETE
    @Path("/{participantId}/{sessionId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response removeParticipantFromSession(@PathParam("participantId") int participantId,
                                                 @PathParam("sessionId") int sessionId) {
        try {
            boolean removed = participantDAO.removeParticipantFromSession(participantId, sessionId);
            if (removed) {
                return Response.status(Response.Status.NO_CONTENT).build();
            } else {
                return Response.status(Response.Status.NOT_FOUND)
                               .entity("Учасник не знайдений або не знайдена відповідна сесія.")
                               .build();
            }
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }
}
