package org.dylanpiergies.contacts.common.logging;

class LogCorrelationProcessingException extends RuntimeException {
    LogCorrelationProcessingException(Throwable e) {
        super("Failed to decode log correlation header.", e);
    }
}
