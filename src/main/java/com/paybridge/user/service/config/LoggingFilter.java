package com.paybridge.user.service.config;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.UUID;

@Component
public class LoggingFilter implements Filter {

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        long start = System.currentTimeMillis();

        // Generate or read trace_id
        String traceId = request.getHeader("X-Trace-Id");
        if (traceId == null || traceId.isEmpty()) {
            traceId = UUID.randomUUID().toString();
        }

        // Put MDC defaults
        MDC.put("service", "paybridge-user-service");
        MDC.put("trace_id", traceId);
        MDC.put("span_id", UUID.randomUUID().toString().replace("-", ""));
        MDC.put("method", request.getMethod());
        MDC.put("endpoint", request.getRequestURI());

        try {
            chain.doFilter(request, response);
        } finally {
            long duration = System.currentTimeMillis() - start;

            // Attach status & duration
            MDC.put("status", String.valueOf(response.getStatus()));
            MDC.put("duration_ms", String.valueOf(duration));

            // Force a log line AFTER request finishes
            // (So every request has a summary log)
            org.slf4j.LoggerFactory.getLogger("HTTP_LOG")
                    .info("HTTP request completed");

            MDC.clear();
        }
    }
}
