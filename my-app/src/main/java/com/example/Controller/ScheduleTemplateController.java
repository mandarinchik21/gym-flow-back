package com.example.Controller;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import com.example.Model.ScheduleTemplate;
import com.example.DatabaseConnection;

@Path("/schedule-templates")
public class ScheduleTemplateController {

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addTemplate(ScheduleTemplate template) {
        String sql = "INSERT INTO schedule_templates (trainer_id, day_of_week, time) VALUES (?, ?, ?)";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, template.getTrainerId());
            stmt.setString(2, template.getDayOfWeek());
            stmt.setTime(3, Time.valueOf(template.getTime()));
            stmt.executeUpdate();
            return Response.status(Response.Status.CREATED).entity("Template added successfully.").build();
        } catch (SQLException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllTemplates() {
        String sql = "SELECT * FROM schedule_templates";
        List<ScheduleTemplate> templates = new ArrayList<>();
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                ScheduleTemplate template = new ScheduleTemplate(
                        rs.getInt("id"),
                        rs.getInt("trainer_id"),
                        rs.getString("day_of_week"),
                        rs.getTime("time").toLocalTime()
                );
                templates.add(template);
            }
            return Response.ok(templates).build();
        } catch (SQLException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }

    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateTemplate(@PathParam("id") int id, ScheduleTemplate template) {
        String sql = "UPDATE schedule_templates SET trainer_id = ?, day_of_week = ?, time = ? WHERE id = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, template.getTrainerId());
            stmt.setString(2, template.getDayOfWeek());
            stmt.setTime(3, Time.valueOf(template.getTime()));
            stmt.setInt(4, id);
            int rowsUpdated = stmt.executeUpdate();
            if (rowsUpdated > 0) {
                return Response.ok("Template updated successfully.").build();
            } else {
                return Response.status(Response.Status.NOT_FOUND).entity("Template not found.").build();
            }
        } catch (SQLException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }

    @DELETE
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteTemplate(@PathParam("id") int id) {
        String sql = "DELETE FROM schedule_templates WHERE id = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            int rowsDeleted = stmt.executeUpdate();
            if (rowsDeleted > 0) {
                return Response.status(Response.Status.NO_CONTENT).build();
            } else {
                return Response.status(Response.Status.NOT_FOUND).entity("Template not found.").build();
            }
        } catch (SQLException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }
}
