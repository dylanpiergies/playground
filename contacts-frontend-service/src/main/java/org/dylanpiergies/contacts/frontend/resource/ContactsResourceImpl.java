package org.dylanpiergies.contacts.frontend.resource;

import org.apache.cxf.jaxrs.client.WebClient;
import org.dylanpiergies.contacts.api.model.Contact;
import org.dylanpiergies.contacts.api.resource.ContactsResource;
import org.dylanpiergies.contacts.common.jaxrs.client.ProxyClientFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import java.util.List;

@Service
public class ContactsResourceImpl implements ContactsResource {

    private final ProxyClientFactory proxyClientFactory;

    @Value("${contacts.backend.baseUrl}")
    private String contactsBackendBaseUrl;

    @Context
    private HttpServletResponse response;

    @Autowired
    public ContactsResourceImpl(ProxyClientFactory proxyClientFactory) {
        this.proxyClientFactory = proxyClientFactory;
    }

    @Override
    public Contact getContact(int id) {
        return createContactsBackendResource().getContact(id);
    }

    @Override
    public List<Contact> getContacts() {
        return createContactsBackendResource().getContacts();
    }

    @Override
    public void createContact(Contact contact) {
        ContactsResource contactsBackendResource = createContactsBackendResource();
        contactsBackendResource.createContact(contact);
        response.setHeader("Location", WebClient.client(contactsBackendResource).getResponse().getHeaderString("Location"));
        response.setStatus(Response.Status.CREATED.getStatusCode());
    }

    @Override
    public void updateContact(Contact contact) {
        ContactsResource contactsBackendResource = createContactsBackendResource();
        contactsBackendResource.updateContact(contact);
    }

    @Override
    public void deleteContact(int id) {
        ContactsResource contactsBackendResource = createContactsBackendResource();
        contactsBackendResource.deleteContact(id);
    }

    private ContactsResource createContactsBackendResource() {
        return proxyClientFactory.createProxyClient(contactsBackendBaseUrl, ContactsResource.class);
    }
}
