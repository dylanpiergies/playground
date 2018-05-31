package org.dylanpiergies.contacts.resource;

import java.util.List;

import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.dylanpiergies.contacts.model.Contact;

@Path("/contacts")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface ContactsResource {

    @GET
    @Path("/{id}")
    Contact getContact(@PathParam("id") int id);

    @GET
    List<Contact> getContacts();

    @POST
    void createContact(@NotNull Contact contact);

    @PUT
    void updateContact(@NotNull Contact contact);

    @DELETE
    @Path("/{id}")
    void deleteContact(@PathParam("id") int id);
}
