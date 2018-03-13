package org.dylanpiergies.contacts.backend.resource;

import org.dylanpiergies.contacts.api.model.Contact;
import org.dylanpiergies.contacts.api.model.TelephoneNumber;
import org.dylanpiergies.contacts.api.resource.ContactsResource;
import org.dylanpiergies.contacts.backend.db.tables.records.ContactRecord;
import org.dylanpiergies.contacts.backend.db.tables.records.TelephoneNumberRecord;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.RecordMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.dylanpiergies.contacts.backend.db.tables.Contact.CONTACT;
import static org.dylanpiergies.contacts.backend.db.tables.TelephoneNumber.TELEPHONE_NUMBER;

@Service
public class ContactsResourceImpl implements ContactsResource {

    @Context
    private HttpServletResponse response;

    private final DSLContext dsl;

    @Autowired
    public ContactsResourceImpl(DSLContext dsl) {
        this.dsl = dsl;
    }

    private RecordMapper<Record, Contact> contactRecordMapper = record -> {
        Contact contact = new Contact();
        contact.setId(record.get(CONTACT.ID));
        contact.setTitle(record.get(CONTACT.TITLE));
        contact.setForenames(record.get(CONTACT.FORENAMES));
        contact.setSurname(record.get(CONTACT.SURNAME));
        return contact;
    };

    private RecordMapper<Record, TelephoneNumber> telephoneNumberRecordMapper = record -> {
        TelephoneNumber telephoneNumber = new TelephoneNumber();
        telephoneNumber.setId(record.get(TELEPHONE_NUMBER.ID));
        telephoneNumber.setType(record.get(TELEPHONE_NUMBER.TYPE));
        telephoneNumber.setNumber(record.get(TELEPHONE_NUMBER.NUMBER));
        return telephoneNumber;
    };

    @Override
    public Contact getContact(final int id) {
        List<Contact> contacts = dsl.select()
            .from(CONTACT)
            .leftJoin(TELEPHONE_NUMBER)
            .on(TELEPHONE_NUMBER.CONTACT_ID.eq(CONTACT.ID))
            .where(CONTACT.ID.eq(id))
            .fetchGroups(contactRecordMapper,
                telephoneNumberRecordMapper).entrySet().stream().map(entry -> {
                Contact contact = entry.getKey();
                contact.setTelephoneNumbers(
                    entry.getValue().stream().filter(telephoneNumber -> telephoneNumber.getId() != null).collect(
                        Collectors.toList()));
                return contact;
            }).collect(Collectors.toList());
        if (contacts.isEmpty()) {
            throw new NotFoundException();
        }
        return contacts.get(0);
    }

    @Override
    public List<Contact> getContacts() {
        Map<Contact, List<TelephoneNumber>> result = dsl.select()
            .from(CONTACT)
            .leftJoin(TELEPHONE_NUMBER)
            .on(TELEPHONE_NUMBER.CONTACT_ID.eq(CONTACT.ID))
            .fetchGroups(contactRecordMapper, telephoneNumberRecordMapper);
        return result.entrySet().stream().map(entry -> {
            Contact contact = entry.getKey();
            contact.setTelephoneNumbers(
                entry.getValue().stream().filter(telephoneNumber -> telephoneNumber.getId() != null).collect(
                    Collectors.toList()));
            return contact;
        }).collect(Collectors.toList());
    }

    @Override
    public void createContact(final Contact contact) {
        final ContactRecord contactsRecord = dsl.newRecord(CONTACT);
        contactsRecord.setTitle(contact.getTitle());
        contactsRecord.setForenames(contact.getForenames());
        contactsRecord.setSurname(contact.getSurname());
        contactsRecord.insert();
        if (contact.getTelephoneNumbers() != null) {
            final List<TelephoneNumberRecord> telephoneNumberRecords = contact.getTelephoneNumbers()
                .stream()
                .map(telephoneNumber -> {
                    final TelephoneNumberRecord telephoneNumberRecord = dsl.newRecord(TELEPHONE_NUMBER);
                    telephoneNumberRecord.setContactId(contactsRecord.getId());
                    telephoneNumberRecord.setType(telephoneNumber.getType());
                    telephoneNumberRecord.setNumber(telephoneNumber.getNumber());
                    return telephoneNumberRecord;
                })
                .collect(Collectors.toList());
            dsl.batchInsert(telephoneNumberRecords).execute();
        }
        response.setHeader("Location", Integer.toString(contactsRecord.getId()));
        response.setStatus(Response.Status.CREATED.getStatusCode());
    }

    @Override
    public void updateContact(Contact contact) {
        throw new WebApplicationException(Response.Status.METHOD_NOT_ALLOWED);
    }
}
