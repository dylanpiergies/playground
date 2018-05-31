package org.dylanpiergies.contacts.common.logging;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public final class LogCorrelationUtils {
    public static final String LOG_CORRELATION_HTTP_HEADER = "X-Log-Correlation";
    public static final String LOG_CORRELATION_MDC_KEY = "correlation";

    public static Map<String, String> parseJsonString(final String correlationString)
            throws LogCorrelationProcessingException {
        if (correlationString != null) {
            final ObjectMapper objectMapper = new ObjectMapper();
            final TypeReference<LinkedHashMap<String, String>> typeReference = new TypeReference<LinkedHashMap<String, String>>() {
            };
            try {
                return objectMapper.readValue(correlationString, typeReference);
            } catch (final IOException e) {
                throw new LogCorrelationProcessingException(e);
            }
        }
        return new LinkedHashMap<>();
    }

    public static String jsonStringFor(final Map<String, String> correlators) {
        final ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.writeValueAsString(correlators);
        } catch (final JsonProcessingException e) {
            throw new LogCorrelationProcessingException(e);
        }
    }
}
