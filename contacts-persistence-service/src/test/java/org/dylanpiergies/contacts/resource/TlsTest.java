package org.dylanpiergies.contacts.resource;

import org.dylanpiergies.contacts.ContactsPersistenceApplication;
import org.dylanpiergies.contacts.common.jaxrs.client.ProxyClientFactory;
import org.dylanpiergies.contacts.test.util.DataFactoryUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
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
    @ComponentScan(basePackageClasses = { ContactsPersistenceApplication.class, ProxyClientFactory.class })
    static class ContextConfiguration {
    }

    @Autowired
    private ProxyClientFactory proxyClientFactory;

    private ContactsResource contactsResource;

    @Before
    public void init() {
        contactsResource = proxyClientFactory.createProxyClient("https://localhost:" + serverPort + "/api",
                ContactsResource.class);
    }

    @Test
    public void testContactsResource() {
        contactsResource.createContact(DataFactoryUtils.createContact());
    }
}
