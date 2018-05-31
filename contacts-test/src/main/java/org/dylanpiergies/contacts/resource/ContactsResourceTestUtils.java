package org.dylanpiergies.contacts.resource;

import static com.shazam.shazamcrest.matcher.Matchers.sameBeanAs;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.cxf.jaxrs.client.WebClient;
import org.dylanpiergies.contacts.model.Contact;
import org.dylanpiergies.contacts.test.util.DataFactoryUtils;

public class ContactsResourceTestUtils {

    public static void testContactsResource(final ContactsResource contactsResource) {
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
