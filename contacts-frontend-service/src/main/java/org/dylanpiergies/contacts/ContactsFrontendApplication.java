package org.dylanpiergies.contacts;

import org.dylanpiergies.contacts.common.jaxrs.client.ProxyClientFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackageClasses = {ContactsFrontendApplication.class, ProxyClientFactory.class})
public class ContactsFrontendApplication {

    public static void main(String[] args) {
        SpringApplication.run(ContactsFrontendApplication.class, args);
    }
}
