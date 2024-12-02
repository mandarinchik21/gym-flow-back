package com.example.Controller;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.time.LocalDate;
import java.util.List;

import com.example.DAO.TrainerDAO;
import com.example.Model.IndividualSession;
import com.example.Model.Trainer;

@Path("/trainers")
public class TrainerController {

    private TrainerDAO trainerDAO = new TrainerDAO();

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createTrainer(Trainer trainer) {
        try {
            trainerDAO.createTrainer(trainer);
            return Response.status(Response.Status.CREATED).entity(trainer).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllTrainers() {
        try {
            List<Trainer> trainers = trainerDAO.getAllTrainers();
            return Response.ok(trainers).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTrainerById(@PathParam("id") int id) {
        try {
            Trainer trainer = trainerDAO.getTrainerById(id);
            if (trainer == null) {
                return Response.status(Response.Status.NOT_FOUND).build();
            }
            return Response.ok(trainer).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }

    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateTrainer(@PathParam("id") int id, Trainer trainer) {
        try {
            trainer.setId(id);
            boolean updated = trainerDAO.updateTrainer(trainer);
            if (!updated) {
                return Response.status(Response.Status.NOT_FOUND).build();
            }
            return Response.ok(trainer).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }

    @DELETE
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteTrainer(@PathParam("id") int id) {
        try {
            boolean deleted = trainerDAO.deleteTrainer(id);
            if (!deleted) {
                return Response.status(Response.Status.NOT_FOUND).build();
            }
            return Response.status(Response.Status.NO_CONTENT).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }

    @GET
    @Path("/{trainerId}/individual-sessions/{date}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAvailableSessionsForTrainer(
            @PathParam("trainerId") int trainerId, 
            @PathParam("date") String dateStr) {
        try {
            LocalDate date = LocalDate.parse(dateStr);  // Перетворюємо дату з рядка в LocalDate
            List<IndividualSession> sessions = trainerDAO.getAvailableSessionsForTrainer(trainerId, date);
            
            if (sessions.isEmpty()) {
                return Response.status(Response.Status.NO_CONTENT).build();
            }
            
            return Response.ok(sessions).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }

    @GET
    @Path("/{trainerId}/individual-sessions")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllSessionsForTrainer(@PathParam("trainerId") int trainerId) {
        try {
            List<IndividualSession> sessions = trainerDAO.getAvailableSessionsForTrainer(trainerId, null);
            
            if (sessions.isEmpty()) {
                return Response.status(Response.Status.NO_CONTENT).build();
            }
            
            return Response.ok(sessions).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }
}