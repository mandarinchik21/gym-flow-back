package com.example.Controller;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

import com.example.DAO.ClientDAO;
import com.example.Model.Client;

@Path("/clients")
public class ClientController {

    private ClientDAO clientDAO = new ClientDAO();

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createClient(Client client) {
        try {
            clientDAO.createClient(client);
            return Response.status(Response.Status.CREATED).entity(client).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllClients() {
        try {
            List<Client> clients = clientDAO.getAllClients();
            return Response.ok(clients).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getClientById(@PathParam("id") int id) {
        try {
            Client client = clientDAO.getClientById(id);
            if (client == null) {
                return Response.status(Response.Status.NOT_FOUND).build();
            }
            return Response.ok(client).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }

    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateClient(@PathParam("id") int id, Client client) {
        try {
            client.setId(id);
            boolean updated = clientDAO.updateClient(client);
            if (!updated) {
                return Response.status(Response.Status.NOT_FOUND).build();
            }
            return Response.ok(client).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }

    @DELETE
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteClient(@PathParam("id") int id) {
        try {
            boolean deleted = clientDAO.deleteClient(id);
            if (!deleted) {
                return Response.status(Response.Status.NOT_FOUND).build();
            }
            return Response.status(Response.Status.NO_CONTENT).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }

    @GET
    @Path("/{id}/has-active-membership")
    @Produces(MediaType.APPLICATION_JSON)
    public Response hasActiveMembership(@PathParam("id") int id) {
        try {
            boolean hasActiveMembership = clientDAO.hasActiveMembership(id);
            if (hasActiveMembership) {
                return Response.ok().entity("{\"hasActiveMembership\": true}").build();
            } else {
                return Response.ok().entity("{\"hasActiveMembership\": false}").build();
            }
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }
}