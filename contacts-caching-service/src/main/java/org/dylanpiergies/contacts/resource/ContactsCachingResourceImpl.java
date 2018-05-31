package org.dylanpiergies.contacts.resource;

import java.util.List;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import org.apache.cxf.jaxrs.client.WebClient;
import org.dylanpiergies.contacts.common.jaxrs.client.ProxyClientFactory;
import org.dylanpiergies.contacts.model.Contact;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ContactsCachingResourceImpl implements ContactsResource {

    private final ProxyClientFactory proxyClientFactory;

    @Value("${contacts.persistence-service.baseUrl}")
    private String contactsPersistenceServiceBaseUrl;

    @Context
    private HttpServletResponse response;

    @Autowired
    public ContactsCachingResourceImpl(final ProxyClientFactory proxyClientFactory) {
        this.proxyClientFactory = proxyClientFactory;
    }

    @Override
    public Contact getContact(final int id) {
        return createContactsPersistenceResource().getContact(id);
    }

    @Override
    public List<Contact> getContacts() {
        return createContactsPersistenceResource().getContacts();
    }

    @Override
    public void createContact(final Contact contact) {
        final ContactsResource contactsPersistenceResource = createContactsPersistenceResource();
        contactsPersistenceResource.createContact(contact);
        response.setHeader("Location",
                WebClient.client(contactsPersistenceResource).getResponse().getHeaderString("Location"));
        response.setStatus(Response.Status.CREATED.getStatusCode());
    }

    @Override
    public void updateContact(final Contact contact) {
        final ContactsResource contactsPersistenceResource = createContactsPersistenceResource();
        contactsPersistenceResource.updateContact(contact);
    }

    @Override
    public void deleteContact(final int id) {
        final ContactsResource contactsPersistenceResource = createContactsPersistenceResource();
        contactsPersistenceResource.deleteContact(id);
    }

    private ContactsResource createContactsPersistenceResource() {
        return proxyClientFactory.createProxyClient(contactsPersistenceServiceBaseUrl, ContactsResource.class);
    }
}
