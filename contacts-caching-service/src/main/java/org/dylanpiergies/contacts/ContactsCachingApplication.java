package org.dylanpiergies.contacts;

import org.dylanpiergies.contacts.common.jaxrs.client.ProxyClientFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackageClasses = { ContactsCachingApplication.class, ProxyClientFactory.class })
public class ContactsCachingApplication {

    public static void main(final String[] args) {
        SpringApplication.run(ContactsCachingApplication.class, args);
    }
}
