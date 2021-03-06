package org.dylanpiergies.contacts.resource;

import java.util.List;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import org.dylanpiergies.contacts.model.Contact;
import org.dylanpiergies.contacts.persistence.ContactsPersistenceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ContactsPersistenceResourceImpl implements ContactsResource {

    @Context
    private HttpServletResponse response;

    @Autowired
    private ContactsPersistenceService contactsPersistenceService;

    @Override
    public Contact getContact(final int id) {
        return contactsPersistenceService.getContactById(id).orElseThrow(NotFoundException::new);
    }

    @Override
    public List<Contact> getContacts() {
        return contactsPersistenceService.getAll();
    }

    @Override
    public void createContact(final Contact contact) {
        final int id = contactsPersistenceService.createContact(contact);
        response.setHeader("Location", Integer.toString(id));
        response.setStatus(Response.Status.CREATED.getStatusCode());
    }

    @Override
    public void updateContact(final Contact contact) {
        throw new WebApplicationException(Response.Status.METHOD_NOT_ALLOWED);
    }

    @Override
    public void deleteContact(final int contactId) {
        throw new WebApplicationException(Response.Status.METHOD_NOT_ALLOWED);
    }
}
