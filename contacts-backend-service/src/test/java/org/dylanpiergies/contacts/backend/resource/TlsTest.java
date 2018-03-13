package org.dylanpiergies.contacts.backend.resource;

import org.dylanpiergies.contacts.ContactsBackendApplication;
import org.dylanpiergies.contacts.api.resource.ContactsResource;
import org.dylanpiergies.contacts.common.jaxrs.client.ProxyClientFactory;
import org.fluttercode.datafactory.impl.DataFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test,test-tls")
public class TlsTest {
    @LocalServerPort
    private int serverPort;

    @Configuration
    @ComponentScan(basePackageClasses = {ContactsBackendApplication.class, ProxyClientFactory.class})
    static class ContextConfiguration {}

    @Autowired
    private ProxyClientFactory proxyClientFactory;

    private ContactsResource contactsResource;

    @Before
    public void init() {
        contactsResource = proxyClientFactory.createProxyClient("https://localhost:" + serverPort + "/api", ContactsResource.class);
    }

    @Test
    public void testContactsResource() {
        contactsResource.createContact(TestUtils.createContact());
    }
}
