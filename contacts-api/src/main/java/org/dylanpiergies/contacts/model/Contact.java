package org.dylanpiergies.contacts.model;

import java.util.List;

public class Contact {

    private Integer id;
    private String title;
    private String forenames;
    private String surname;
    private List<TelephoneNumber> telephoneNumbers;

    public Integer getId() {
        return id;
    }

    public void setId(final Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(final String title) {
        this.title = title;
    }

    public String getForenames() {
        return forenames;
    }

    public void setForenames(final String forenames) {
        this.forenames = forenames;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(final String surname) {
        this.surname = surname;
    }

    public List<TelephoneNumber> getTelephoneNumbers() {
        return telephoneNumbers;
    }

    public void setTelephoneNumbers(final List<TelephoneNumber> telephoneNumbers) {
        this.telephoneNumbers = telephoneNumbers;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Contact other = (Contact) obj;
        if (id == null) {
            if (other.id != null) {
                return false;
            }
        } else if (!id.equals(other.id)) {
            return false;
        }
        return true;
    }
}
