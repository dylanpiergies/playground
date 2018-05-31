package org.dylanpiergies.contacts.common.logging;

class LogCorrelationProcessingException extends RuntimeException {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    LogCorrelationProcessingException(final Throwable e) {
        super("Failed to decode log correlation header.", e);
    }
}
