package com.example.Controller;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

import com.example.DAO.PaymentDAO;
import com.example.Model.Payment;

@Path("/payments")
public class PaymentController {

    private PaymentDAO paymentDAO = new PaymentDAO();

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllPayments() {
        try {
            List<Payment> payments = paymentDAO.getAllPayments();
            return Response.ok(payments).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPaymentById(@PathParam("id") int id) {
        try {
            Payment payment = paymentDAO.getPaymentById(id);
            if (payment == null) {
                return Response.status(Response.Status.NOT_FOUND).build();
            }
            return Response.ok(payment).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }

    @GET
    @Path("/client/{clientId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPaymentByClientId(@PathParam("clientId") int clientId) {
        try {
            List<Payment> payments = paymentDAO.getPaymentsByClientId(clientId);
            if (payments.isEmpty()) {
                return Response.status(Response.Status.NOT_FOUND).build();
            }
            return Response.ok(payments).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createPayment(Payment payment) {
        try {
            paymentDAO.createPayment(payment);
            return Response.status(Response.Status.CREATED).entity(payment).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }

    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updatePayment(@PathParam("id") int id, Payment payment) {
        try {
            payment.setId(id);
            boolean updated = paymentDAO.updatePayment(payment);
            if (!updated) {
                return Response.status(Response.Status.NOT_FOUND).build();
            }
            return Response.ok(payment).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }

    @DELETE
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deletePayment(@PathParam("id") int id) {
        try {
            boolean deleted = paymentDAO.deletePayment(id);
            if (!deleted) {
                return Response.status(Response.Status.NOT_FOUND).build();
            }
            return Response.status(Response.Status.NO_CONTENT).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }
}
