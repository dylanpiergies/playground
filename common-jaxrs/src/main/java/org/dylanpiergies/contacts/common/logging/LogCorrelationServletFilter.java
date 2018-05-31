package org.dylanpiergies.contacts.common.logging;

import static org.dylanpiergies.contacts.common.logging.LogCorrelationUtils.LOG_CORRELATION_HTTP_HEADER;
import static org.dylanpiergies.contacts.common.logging.LogCorrelationUtils.LOG_CORRELATION_MDC_KEY;
import static org.dylanpiergies.contacts.common.logging.LogCorrelationUtils.jsonStringFor;
import static org.dylanpiergies.contacts.common.logging.LogCorrelationUtils.parseJsonString;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.BadRequestException;

import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class LogCorrelationServletFilter implements Filter {
    @Value("${server.log-correlation.accept-http-header:true}")
    private boolean acceptHttpHeader;

    @Override
    public void init(final FilterConfig filterConfig) throws ServletException {
        // Nothing to do.
    }

    @Override
    public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain)
            throws IOException, ServletException {
        if (request instanceof HttpServletRequest) {
            final HttpServletRequest httpServletRequest = (HttpServletRequest) request;
            Map<String, String> correlators;
            if (acceptHttpHeader) {
                try {
                    correlators = parseJsonString(httpServletRequest.getHeader(LOG_CORRELATION_HTTP_HEADER));
                } catch (final LogCorrelationProcessingException e) {
                    throw new BadRequestException(e);
                }
            } else {
                correlators = new LinkedHashMap<>();
            }
            if (!correlators.containsKey("uuid")) {
                correlators.put("uuid", UUID.randomUUID().toString());
            }
            MDC.put(LOG_CORRELATION_MDC_KEY, jsonStringFor(correlators));
        }
        try {
            chain.doFilter(request, response);
        } finally {
            MDC.remove(LOG_CORRELATION_MDC_KEY);
        }
    }

    @Override
    public void destroy() {
        // Nothing to do.
    }
}
