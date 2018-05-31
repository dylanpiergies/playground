package org.dylanpiergies.contacts.resource;

import java.util.List;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.NotAllowedException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.dylanpiergies.contacts.caching.CachingContactsService;
import org.dylanpiergies.contacts.model.Contact;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.RequestScope;

@Service
@RequestScope
public class ContactsCachingResourceImpl implements ContactsResource {

    @Autowired
    private CachingContactsService contactsService;

    @Context
    private HttpServletResponse response;

    @Override
    public Contact getContact(final int id) {
        return contactsService.findById(id).orElseThrow(NotFoundException::new);
    }

    @Override
    public List<Contact> getContacts() {
        return contactsService.getAll();
    }

    @Override
    public void createContact(final Contact contact) {
        final int id = contactsService.create(contact);
        response.setHeader("Location", Integer.toString(id));
        response.setStatus(Response.Status.CREATED.getStatusCode());
    }

    @Override
    public void updateContact(final Contact contact) {
        throw new NotAllowedException(Response.status(Status.METHOD_NOT_ALLOWED).build());
    }

    @Override
    public void deleteContact(final int id) {
        throw new NotAllowedException(Response.status(Status.METHOD_NOT_ALLOWED).build());
    }
}
