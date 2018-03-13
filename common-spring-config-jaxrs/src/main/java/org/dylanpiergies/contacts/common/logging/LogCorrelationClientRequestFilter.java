package org.dylanpiergies.contacts.common.logging;

import org.slf4j.MDC;

import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;

import static org.dylanpiergies.contacts.common.logging.LogCorrelationUtils.*;

@Provider
public class LogCorrelationClientRequestFilter implements ClientRequestFilter {

    @Override
    public void filter(ClientRequestContext requestContext) throws IOException {
        Map<String, String> correlators = parseJsonString(MDC.get(LOG_CORRELATION_MDC_KEY));
        if (!correlators.containsKey("uuid")) {
            correlators.put("uuid", UUID.randomUUID().toString());
        }
        requestContext.getStringHeaders().add(LOG_CORRELATION_HTTP_HEADER, jsonStringFor(correlators));
    }
}
