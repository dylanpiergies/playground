package org.dylanpiergies.contacts;

import org.dylanpiergies.contacts.common.jaxrs.client.ProxyClientFactory;
import org.dylanpiergies.contacts.resource.ContactsResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.context.annotation.RequestScope;

@SpringBootApplication(scanBasePackageClasses = { ContactsCachingApplication.class, ProxyClientFactory.class })
public class ContactsCachingApplication {

    @Value("${contacts.persistence-service.baseUrl}")
    private String contactsPersistenceServiceBaseUrl;

    @Autowired
    private ProxyClientFactory proxyClientFactory;

    public static void main(final String[] args) {
        SpringApplication.run(ContactsCachingApplication.class, args);
    }

    @Bean("contactsPersistenceResource")
    @RequestScope
    public ContactsResource contactsPersistenceResource() {
        return proxyClientFactory.createProxyClient(contactsPersistenceServiceBaseUrl, ContactsResource.class);
    }
}
