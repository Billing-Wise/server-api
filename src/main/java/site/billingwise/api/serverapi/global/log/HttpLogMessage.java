package site.billingwise.api.serverapi.global.log;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.stream.Collectors;

@Value
public class HttpLogMessage {
    private static final ObjectMapper objectMapper = new ObjectMapper()
            .enable(SerializationFeature.INDENT_OUTPUT);
    private static final String EMPTY_BODY = "   <empty>";
    private static final String LOG_FORMAT = """
            
            [REQUEST] %s %s %s (%.3f)
            >> CLIENT_IP: %s
            >> HEADERS:
            %s
            >> REQUEST_PARAM: %s
            >> REQUEST_BODY:
            %s
            >> RESPONSE_BODY:
            %s
            """;

    String httpMethod;
    String requestUri;
    HttpStatus httpStatus;
    String clientIp;
    Double elapsedTime;
    String headers;
    String requestParam;
    String requestBody;
    String responseBody;

    public static HttpLogMessage createFrom(
            ContentCachingRequestWrapper requestWrapper,
            ContentCachingResponseWrapper responseWrapper,
            Double elapsedTime
    ) {
        return new HttpLogMessage(
                requestWrapper.getMethod(),
                requestWrapper.getRequestURI(),
                HttpStatus.valueOf(responseWrapper.getStatus()),
                requestWrapper.getRemoteAddr(),
                elapsedTime,
                getRequestHeaders(requestWrapper),
                getRequestParams(requestWrapper),
                getRequestBody(requestWrapper),
                getResponseBody(responseWrapper)
        );
    }

    private static String getRequestHeaders(ContentCachingRequestWrapper request) {
        return Collections.list(request.getHeaderNames()).stream()
                .map(headerName -> headerName + ": " + request.getHeader(headerName))
                .collect(Collectors.joining("\n"));
    }

    private static String getRequestParams(ContentCachingRequestWrapper request) {
        return request.getParameterMap().entrySet().stream()
                .map(entry -> entry.getKey() + "=" + String.join(",", entry.getValue()))
                .collect(Collectors.joining("&"));
    }

    private static String getRequestBody(ContentCachingRequestWrapper request) {
        return new String(request.getContentAsByteArray(), StandardCharsets.UTF_8);
    }

    private static String getResponseBody(ContentCachingResponseWrapper response) {
        String rawBody = new String(response.getContentAsByteArray(), StandardCharsets.UTF_8);
        try {
            return objectMapper.writeValueAsString(objectMapper.readTree(rawBody));
        } catch (Exception e) {
            return rawBody;
        }
    }

    public String toPrettierLog() {
        return String.format(LOG_FORMAT,
                this.httpMethod, this.requestUri, this.httpStatus, this.elapsedTime,
                this.clientIp,
                formatMultiline(this.headers),
                this.requestParam,
                formatMultiline(this.requestBody),
                formatMultiline(this.responseBody)
        );
    }

    private String formatMultiline(String content) {
        if (content == null || content.isEmpty()) {
            return EMPTY_BODY;
        }
        return Arrays.stream(content.split("\n"))
                .map(line -> "   " + line)
                .collect(Collectors.joining("\n"));
    }
}