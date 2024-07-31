package site.billingwise.api.serverapi.global.log;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ApiLoggingFilter extends OncePerRequestFilter {
    private static final Logger log = LoggerFactory.getLogger(ApiLoggingFilter.class);
    private static final String REQUEST_ID = "request_id";
    private static final String API_PATH = "/api";

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        if (!isApiRequest(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        ContentCachingRequestWrapper cachingRequestWrapper = new ContentCachingRequestWrapper(request);
        ContentCachingResponseWrapper cachingResponseWrapper = new ContentCachingResponseWrapper(response);

        String requestId = generateRequestId();
        MDC.put(REQUEST_ID, requestId);

        long startTime = System.currentTimeMillis();
        try {
            filterChain.doFilter(cachingRequestWrapper, cachingResponseWrapper);
        } finally {
            long endTime = System.currentTimeMillis();
            logRequest(cachingRequestWrapper, cachingResponseWrapper, startTime, endTime);
            MDC.remove(REQUEST_ID);
        }
    }

    private boolean isApiRequest(HttpServletRequest request) {
        return request.getRequestURI().startsWith(API_PATH);
    }

    private String generateRequestId() {
        return UUID.randomUUID().toString().substring(0, 8);
    }

    private void logRequest(ContentCachingRequestWrapper requestWrapper,
                            ContentCachingResponseWrapper responseWrapper,
                            long startTime, long endTime) {
        try {
            double elapsedTime = (endTime - startTime) / 1000.0;
            ApiLogMessage logMessage = ApiLogMessage.createFrom(requestWrapper, responseWrapper, elapsedTime);
            log.info("REQUEST_LOG: " + logMessage.toJsonLog());
            responseWrapper.copyBodyToResponse();
        } catch (Exception e) {
            log.error("Failed to log request", e);
        }
    }
}