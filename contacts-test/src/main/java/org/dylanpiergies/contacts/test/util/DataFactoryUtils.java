package org.dylanpiergies.contacts.test.util;

import java.util.ArrayList;

import org.dylanpiergies.contacts.model.Contact;
import org.dylanpiergies.contacts.model.TelephoneNumber;
import org.fluttercode.datafactory.impl.DataFactory;

public final class DataFactoryUtils {
    private static final String[] TITLES = { "Mr.", "Mrs.", "Miss", "Dr." };
    private static final String[] TELEPHONE_NUMBER_TYPES = { "Work", "Home", "Mobile" };

    private static final DataFactory dataFactory = new DataFactory();

    private DataFactoryUtils() {
        // Prevent instantiation
    }

    public static Contact createContact() {
        final Contact contact = new Contact();
        contact.setTitle(dataFactory.getItem(TITLES));
        contact.setForenames(dataFactory.getFirstName());
        contact.setSurname(dataFactory.getLastName());
        contact.setTelephoneNumbers(new ArrayList<>());
        for (int i = 0; i < dataFactory.getNumberBetween(0, 10); i++) {
            final TelephoneNumber telephoneNumber = new TelephoneNumber();
            telephoneNumber.setType(dataFactory.getItem(TELEPHONE_NUMBER_TYPES));
            telephoneNumber.setNumber('0' + dataFactory.getNumberText(4) + ' ' + dataFactory.getNumberText(6));
            contact.getTelephoneNumbers().add(telephoneNumber);
        }
        return contact;
    }
}
