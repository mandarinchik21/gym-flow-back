package com.example.Controller;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.sql.SQLException;
import java.util.List;

import com.example.DAO.MembershipDAO;
import com.example.DAO.SessionService;
import com.example.Model.Membership;

@Path("/memberships")
public class MembershipController {

    private MembershipDAO membershipDAO = new MembershipDAO();
    private SessionService sessionService;

    public MembershipController() {
        this.sessionService = new SessionService();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createMembership(Membership membership) {
        try {
            membershipDAO.createMembership(membership);
            return Response.status(Response.Status.CREATED).entity(membership).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllMemberships() {
        try {
            List<Membership> memberships = membershipDAO.getAllMemberships();
            return Response.ok(memberships).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getMembershipById(@PathParam("id") int id) {
        try {
            Membership membership = membershipDAO.getMembershipById(id);
            if (membership == null) {
                return Response.status(Response.Status.NOT_FOUND).build();
            }
            return Response.ok(membership).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }

    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateMembership(@PathParam("id") int id, Membership membership) {
        try {
            membership.setId(id);
            boolean updated = membershipDAO.updateMembership(membership);
            if (!updated) {
                return Response.status(Response.Status.NOT_FOUND).build();
            }
            return Response.ok(membership).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }

    @DELETE
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteMembership(@PathParam("id") int id) {
        try {
            boolean deleted = membershipDAO.deleteMembership(id);
            if (!deleted) {
                return Response.status(Response.Status.NOT_FOUND).build();
            }
            return Response.status(Response.Status.NO_CONTENT).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }

    @GET
    @Path("/client/{clientId}")
    @Produces("application/json")
    public Membership getClientMembership(@PathParam("clientId") int clientId) {
        try {
            return sessionService.getClientMembership(clientId);
        } catch (SQLException e) {
            throw new WebApplicationException("Error retrieving membership: " + e.getMessage(), 500);
        }
    }
}