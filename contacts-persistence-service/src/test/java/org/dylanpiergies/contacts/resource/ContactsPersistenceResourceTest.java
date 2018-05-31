package org.dylanpiergies.contacts.resource;

import static com.shazam.shazamcrest.matcher.Matchers.sameBeanAs;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Context;

import org.apache.cxf.jaxrs.client.WebClient;
import org.dylanpiergies.contacts.ContactsPersistenceApplication;
import org.dylanpiergies.contacts.common.jaxrs.client.ProxyClientFactory;
import org.dylanpiergies.contacts.model.Contact;
import org.dylanpiergies.contacts.test.util.DataFactoryUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class ContactsPersistenceResourceTest {
    @LocalServerPort
    private int serverPort;

    @Value("${cxf.path}")
    private String apiBasePath;

    @Configuration
    @ComponentScan(basePackageClasses = { ContactsPersistenceApplication.class, ProxyClientFactory.class })
    static class ContextConfiguration {
    }

    @Autowired
    private ProxyClientFactory proxyClientFactory;

    private ContactsResource contactsResource;

    @Context
    private HttpServletResponse response;

    @Before
    public void init() {
        contactsResource = proxyClientFactory.createProxyClient("http://localhost:" + serverPort + apiBasePath,
                ContactsResource.class);
    }

    @Test
    public void testContactsResource() {
        final List<Contact> contacts = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            contacts.add(DataFactoryUtils.createContact());
        }
        final Set<Integer> createdIds = new HashSet<>();
        contacts.forEach(contact -> {
            contactsResource.createContact(contact);
            final int id = Integer
                    .valueOf(WebClient.client(contactsResource).getResponse().getHeaderString("Location"));
            contact.setId(id);
            createdIds.add(id);
        });

        final List<Contact> retrievedContacts = contactsResource.getContacts()
                .stream()
                .filter(contact -> createdIds.contains(contact.getId()))
                .collect(Collectors.toList());
        assertThat(retrievedContacts, sameBeanAs(contacts).ignoring("telephoneNumbers.id"));

        contacts.forEach(contact -> {
            final Contact retrievedContact = contactsResource.getContact(contact.getId());
            assertThat(retrievedContact, sameBeanAs(contact).ignoring("telephoneNumbers.id"));
        });
    }
}
