package org.dylanpiergies.contacts.frontend.resource;

import org.apache.cxf.jaxrs.client.WebClient;
import org.dylanpiergies.contacts.ContactsFrontendApplication;
import org.dylanpiergies.contacts.api.model.Contact;
import org.dylanpiergies.contacts.api.model.TelephoneNumber;
import org.dylanpiergies.contacts.api.resource.ContactsResource;
import org.dylanpiergies.contacts.common.jaxrs.client.ProxyClientFactory;
import org.fluttercode.datafactory.impl.DataFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Context;
import java.util.ArrayList;
import java.util.List;

import static com.shazam.shazamcrest.matcher.Matchers.sameBeanAs;
import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles("dev")
public class ContactsResourceTest {
    private static final String[] TITLES = {"Mr.", "Mrs.", "Miss", "Dr."};
    private static final String[] TELEPHONE_NUMBER_TYPES = {"Work", "Home", "Mobile"};

    @LocalServerPort
    private int serverPort;

    @Configuration
    @ComponentScan(basePackageClasses = {ContactsFrontendApplication.class, ProxyClientFactory.class})
    static class ContextConfiguration {}

    @Autowired
    private ProxyClientFactory proxyClientFactory;

    private ContactsResource contactsResource;

    private DataFactory dataFactory = new DataFactory();

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
            Contact contact = new Contact();
            contact.setTitle(dataFactory.getItem(TITLES));
            contact.setForenames(dataFactory.getFirstName());
            contact.setSurname(dataFactory.getLastName());
            contact.setTelephoneNumbers(new ArrayList<>());
            for (int j = 0; j < dataFactory.getNumberBetween(0, 10); j++) {
                TelephoneNumber telephoneNumber = new TelephoneNumber();
                telephoneNumber.setType(dataFactory.getItem(TELEPHONE_NUMBER_TYPES));
                telephoneNumber.setNumber(dataFactory.getNumberText(10));
                contact.getTelephoneNumbers().add(telephoneNumber);
            }
            contacts.add(contact);
        }
        contacts.forEach(contact -> {
            contactsResource.createContact(contact);
            contact.setId(Integer.valueOf(WebClient.client(contactsResource).getResponse().getHeaderString("Location")));
        });
        contacts.forEach(contact -> {
            Contact retrievedContact = contactsResource.getContact(contact.getId());
            assertThat(retrievedContact, sameBeanAs(contact).ignoring("telephoneNumbers.id"));
        });
    }
}
