package org.dylanpiergies.contacts.common.logging;

import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.BadRequestException;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

import static org.dylanpiergies.contacts.common.logging.LogCorrelationUtils.*;

@Component
public class LogCorrelationServletFilter implements Filter {
    @Value("${server.log-correlation.accept-http-header:true}")
    private boolean acceptHttpHeader;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Nothing to do.
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        if (request instanceof HttpServletRequest) {
            HttpServletRequest httpServletRequest = (HttpServletRequest) request;
            Map<String, String> correlators;
            if (acceptHttpHeader) {
                try {
                    correlators = parseJsonString(httpServletRequest.getHeader(LOG_CORRELATION_HTTP_HEADER));
                } catch (LogCorrelationProcessingException e) {
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
