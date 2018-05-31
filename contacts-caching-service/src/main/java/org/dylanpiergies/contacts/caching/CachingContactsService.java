package org.dylanpiergies.contacts.caching;

import java.util.List;
import java.util.Optional;

import javax.ws.rs.NotFoundException;

import org.apache.cxf.jaxrs.client.WebClient;
import org.dylanpiergies.contacts.model.Contact;
import org.dylanpiergies.contacts.resource.ContactsResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CachingContactsService {

    @Autowired
    private ContactsResource contactsPersistenceResource;

    public Optional<Contact> findById(final int id) {
        // TODO: Do some caching
        try {
            final Contact contact = contactsPersistenceResource.getContact(id);
            return Optional.of(contact);
        } catch (final NotFoundException e) {
            return Optional.empty();
        }
    }

    public List<Contact> getAll() {
        // TODO: Do some caching
        return contactsPersistenceResource.getContacts();
    }

    public int create(final Contact contact) {
        // TODO: Do some caching
        contactsPersistenceResource.createContact(contact);
        return Integer
                .valueOf(WebClient.client(contactsPersistenceResource).getResponse().getHeaderString("Location"));
    }
}
