package org.dylanpiergies.contacts.backend.resource;

import org.apache.cxf.jaxrs.client.WebClient;
import org.dylanpiergies.contacts.ContactsBackendApplication;
import org.dylanpiergies.contacts.api.model.Contact;
import org.dylanpiergies.contacts.api.resource.ContactsResource;
import org.dylanpiergies.contacts.common.jaxrs.client.ProxyClientFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Context;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.shazam.shazamcrest.matcher.Matchers.sameBeanAs;
import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class ContactsResourceTest {
    @LocalServerPort
    private int serverPort;

    @Configuration
    @ComponentScan(basePackageClasses = {ContactsBackendApplication.class, ProxyClientFactory.class})
    static class ContextConfiguration {
    }

    @Autowired
    private ProxyClientFactory proxyClientFactory;

    private ContactsResource contactsResource;

    @Context
    private HttpServletResponse response;

    @Before
    public void init() {
        contactsResource = proxyClientFactory.createProxyClient("http://localhost:" + serverPort + "/api", ContactsResource.class);
    }

    @Test
    public void testContactsResource() {
        List<Contact> contacts = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            contacts.add(TestUtils.createContact());
        }
        Set<Integer> createdIds = new HashSet<>();
        contacts.forEach(contact -> {
            contactsResource.createContact(contact);
            int id = Integer.valueOf(WebClient.client(contactsResource).getResponse().getHeaderString("Location"));
            contact.setId(id);
            createdIds.add(id);
        });

        List<Contact> retrievedContacts = contactsResource.getContacts().stream()
            .filter(contact -> createdIds.contains(contact.getId())).collect(Collectors.toList());
        assertThat(retrievedContacts, sameBeanAs(contacts).ignoring("telephoneNumbers.id"));

        contacts.forEach(contact -> {
            Contact retrievedContact = contactsResource.getContact(contact.getId());
            assertThat(retrievedContact, sameBeanAs(contact).ignoring("telephoneNumbers.id"));
        });
    }
}
