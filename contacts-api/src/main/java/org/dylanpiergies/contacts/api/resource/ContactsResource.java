package org.dylanpiergies.contacts.api.resource;

import org.dylanpiergies.contacts.api.model.Contact;

import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

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
