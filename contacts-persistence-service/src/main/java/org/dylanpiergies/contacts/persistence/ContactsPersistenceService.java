package org.dylanpiergies.contacts.persistence;

import static org.dylanpiergies.contacts.persistence.jooq.tables.Contact.CONTACT;
import static org.dylanpiergies.contacts.persistence.jooq.tables.TelephoneNumber.TELEPHONE_NUMBER;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.dylanpiergies.contacts.model.Contact;
import org.dylanpiergies.contacts.model.TelephoneNumber;
import org.dylanpiergies.contacts.persistence.jooq.tables.records.ContactRecord;
import org.dylanpiergies.contacts.persistence.jooq.tables.records.TelephoneNumberRecord;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.RecordMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ContactsPersistenceService {

    private final DSLContext dsl;

    @Autowired
    public ContactsPersistenceService(final DSLContext dsl) {
        this.dsl = dsl;
    }

    private final RecordMapper<Record, Contact> contactRecordMapper = record -> {
        final Contact contact = new Contact();
        contact.setId(record.get(CONTACT.ID));
        contact.setTitle(record.get(CONTACT.TITLE));
        contact.setForenames(record.get(CONTACT.FORENAMES));
        contact.setSurname(record.get(CONTACT.SURNAME));
        return contact;
    };

    private final RecordMapper<Record, TelephoneNumber> telephoneNumberRecordMapper = record -> {
        final TelephoneNumber telephoneNumber = new TelephoneNumber();
        telephoneNumber.setId(record.get(TELEPHONE_NUMBER.ID));
        telephoneNumber.setType(record.get(TELEPHONE_NUMBER.TYPE));
        telephoneNumber.setNumber(record.get(TELEPHONE_NUMBER.NUMBER));
        return telephoneNumber;
    };

    public Optional<Contact> getContactById(final int id) {
        final List<Contact> contacts = dsl.select()
                .from(CONTACT)
                .leftJoin(TELEPHONE_NUMBER)
                .on(TELEPHONE_NUMBER.CONTACT_ID.eq(CONTACT.ID))
                .where(CONTACT.ID.eq(id))
                .fetchGroups(contactRecordMapper, telephoneNumberRecordMapper)
                .entrySet()
                .stream()
                .map(entry -> {
                    final Contact contact = entry.getKey();
                    contact.setTelephoneNumbers(entry.getValue()
                            .stream()
                            .filter(telephoneNumber -> telephoneNumber.getId() != null)
                            .collect(Collectors.toList()));
                    return contact;
                })
                .collect(Collectors.toList());
        if (contacts.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(contacts.get(0));
    }

    public List<Contact> getAll() {
        final Map<Contact, List<TelephoneNumber>> result = dsl.select()
                .from(CONTACT)
                .leftJoin(TELEPHONE_NUMBER)
                .on(TELEPHONE_NUMBER.CONTACT_ID.eq(CONTACT.ID))
                .fetchGroups(contactRecordMapper, telephoneNumberRecordMapper);
        return result.entrySet().stream().map(entry -> {
            final Contact contact = entry.getKey();
            contact.setTelephoneNumbers(
                    entry.getValue().stream().filter(telephoneNumber -> telephoneNumber.getId() != null).collect(
                            Collectors.toList()));
            return contact;
        }).collect(Collectors.toList());
    }

    public int createContact(final Contact contact) {
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
        return contactsRecord.getId();
    }
}
