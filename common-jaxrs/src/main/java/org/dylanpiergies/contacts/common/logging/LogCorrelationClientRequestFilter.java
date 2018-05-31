package org.dylanpiergies.contacts.common.logging;

import static org.dylanpiergies.contacts.common.logging.LogCorrelationUtils.LOG_CORRELATION_HTTP_HEADER;
import static org.dylanpiergies.contacts.common.logging.LogCorrelationUtils.LOG_CORRELATION_MDC_KEY;
import static org.dylanpiergies.contacts.common.logging.LogCorrelationUtils.jsonStringFor;
import static org.dylanpiergies.contacts.common.logging.LogCorrelationUtils.parseJsonString;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.ext.Provider;

import org.slf4j.MDC;

@Provider
public class LogCorrelationClientRequestFilter implements ClientRequestFilter {

    @Override
    public void filter(final ClientRequestContext requestContext) throws IOException {
        final Map<String, String> correlators = parseJsonString(MDC.get(LOG_CORRELATION_MDC_KEY));
        if (!correlators.containsKey("uuid")) {
            correlators.put("uuid", UUID.randomUUID().toString());
        }
        requestContext.getStringHeaders().add(LOG_CORRELATION_HTTP_HEADER, jsonStringFor(correlators));
    }
}
