package org.dylanpiergies.contacts.common.logging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.dylanpiergies.contacts.common.model.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

public final class LogCorrelationUtils {
    public static final String LOG_CORRELATION_HTTP_HEADER = "X-Log-Correlation";
    public static final String LOG_CORRELATION_MDC_KEY = "correlation";

    private static final Logger LOGGER = LoggerFactory.getLogger(LogCorrelationUtils.class);

    public static Map<String, String> parseJsonString(String correlationString) throws LogCorrelationProcessingException {
        if (correlationString != null) {
            ObjectMapper objectMapper = new ObjectMapper();
            TypeReference<LinkedHashMap<String, String>> typeReference = new TypeReference<LinkedHashMap<String, String>>() {
            };
            try {
                return objectMapper.readValue(correlationString, typeReference);
            } catch (IOException e) {
                throw new LogCorrelationProcessingException(e);
            }
        }
        return new LinkedHashMap<>();
    }

    public static String jsonStringFor(Map<String, String> correlators) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.writeValueAsString(correlators);
        } catch (JsonProcessingException e) {
            throw new LogCorrelationProcessingException(e);
        }
    }

    public static void addIdentifier(Identifier identifier) {
        Map<String, String> correlators = parseJsonString(MDC.get(LOG_CORRELATION_MDC_KEY));
        if (!correlators.isEmpty()) {
            String className = identifier.getClass().getSimpleName();
            correlators.put(className, identifier.getId().toString());
            MDC.put(LOG_CORRELATION_MDC_KEY, jsonStringFor(correlators));
        }
    }
}
