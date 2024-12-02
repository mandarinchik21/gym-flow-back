package com.example.Controller;

import com.example.Model.TrainerSchedule;
import com.example.DAO.TrainerScheduleDAO;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("/trainer-schedule")
public class TrainerScheduleController {

    private TrainerScheduleDAO trainerScheduleDAO;

    public TrainerScheduleController() {
        this.trainerScheduleDAO = new TrainerScheduleDAO();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllTrainerSchedules() {
        try {
            List<TrainerSchedule> schedules = trainerScheduleDAO.getAllSchedules();
            return Response.ok(schedules).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createTrainerSchedule(TrainerSchedule session) {
        try {
            trainerScheduleDAO.createTrainerSchedule(session);
            return Response.status(Response.Status.CREATED).entity(session).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }

    @DELETE
    @Path("/{trainerId}/{sessionDate}/{sessionTime}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteTrainerSchedule(
            @PathParam("trainerId") int trainerId,
            @PathParam("sessionDate") String sessionDate,
            @PathParam("sessionTime") String sessionTime) {

        try {
            boolean deleted = trainerScheduleDAO.deleteSchedule(trainerId, sessionDate, sessionTime);
            if (!deleted) {
                return Response.status(Response.Status.NOT_FOUND).build();
            }
            return Response.status(Response.Status.NO_CONTENT).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }
}
