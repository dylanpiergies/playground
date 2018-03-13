package org.dylanpiergies.contacts.backend.resource;

import org.dylanpiergies.contacts.api.model.Contact;
import org.dylanpiergies.contacts.api.model.TelephoneNumber;
import org.fluttercode.datafactory.impl.DataFactory;

import java.util.ArrayList;

public final class TestUtils {
    private static final String[] TITLES = {"Mr.", "Mrs.", "Miss", "Dr."};
    private static final String[] TELEPHONE_NUMBER_TYPES = {"Work", "Home", "Mobile"};

    private static final DataFactory dataFactory = new DataFactory();

    private TestUtils() {
        // Prevent instantiation
    }

    public static Contact createContact() {
        Contact contact = new Contact();
        contact.setTitle(dataFactory.getItem(TITLES));
        contact.setForenames(dataFactory.getFirstName());
        contact.setSurname(dataFactory.getLastName());
        contact.setTelephoneNumbers(new ArrayList<>());
        for (int i = 0; i < dataFactory.getNumberBetween(0, 10); i++) {
            TelephoneNumber telephoneNumber = new TelephoneNumber();
            telephoneNumber.setType(dataFactory.getItem(TELEPHONE_NUMBER_TYPES));
            telephoneNumber.setNumber('0' + dataFactory.getNumberText(4) + ' ' + dataFactory.getNumberText(6));
            contact.getTelephoneNumbers().add(telephoneNumber);
        }
        return contact;
    }
}
