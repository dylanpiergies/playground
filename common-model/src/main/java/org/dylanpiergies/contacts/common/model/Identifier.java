package org.dylanpiergies.contacts.common.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public abstract class Identifier<T> {
    private final T id;

    @JsonCreator
    protected Identifier(T id) {
        this.id = id;
    }

    @JsonValue
    public final T getId() {
        return id;
    }
}
