package site.billingwise.api.serverapi.global.log;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.stream.Collectors;

@Value
@JsonInclude(JsonInclude.Include.NON_NULL)
public class HttpLogMessage {
    String httpMethod;
    String requestUri;
    String httpStatus;
    Double elapsedTime;
    String clientIp;
    String headers;
    String requestParam;
    String requestBody;
    String responseBody;

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static HttpLogMessage createFrom(
            ContentCachingRequestWrapper requestWrapper,
            ContentCachingResponseWrapper responseWrapper,
            Double elapsedTime
    ) {
        String httpMethod = requestWrapper.getMethod();
        String requestUri = requestWrapper.getRequestURI();
        String httpStatus = HttpStatus.valueOf(responseWrapper.getStatus()).toString();
        String clientIp = requestWrapper.getRemoteAddr();

        String headers = getRequestHeaders(requestWrapper);
        String requestParam = getRequestParams(requestWrapper);
        String requestBody = getRequestBody(requestWrapper);
        String responseBody = getResponseBody(responseWrapper);

        return new HttpLogMessage(
                httpMethod,
                requestUri,
                httpStatus,
                elapsedTime,
                clientIp,
                headers,
                requestParam,
                requestBody,
                responseBody
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
        return new String(response.getContentAsByteArray(), StandardCharsets.UTF_8);
    }

    public String toLogString() {
        return String.format("method=%s uri=%s status=%s time=%.3f ip=%s headers=%s params=%s body=%s response=%s",
                httpMethod,
                requestUri,
                httpStatus,
                elapsedTime,
                clientIp,
                headers.replace("\n", "\\n"),
                requestParam,
                requestBody.replace("\n", "\\n").replace("\r", ""),
                responseBody.replace("\n", "\\n").replace("\r", "")
        );
    }
}