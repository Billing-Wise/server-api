package site.billingwise.api.serverapi.global.log;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.stream.Collectors;

@Value
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiLogMessage {
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

    public static ApiLogMessage createFrom(
            ContentCachingRequestWrapper requestWrapper,
            ContentCachingResponseWrapper responseWrapper,
            Double elapsedTime
    ) {
        String httpMethod = requestWrapper.getMethod();
        String requestUri = requestWrapper.getRequestURI();
        String httpStatus = HttpStatus.valueOf(responseWrapper.getStatus()).toString();
        String clientIp = getClientIp(requestWrapper);

        String headers = getRequestHeaders(requestWrapper);
        String requestParam = getRequestParams(requestWrapper);
        String requestBody = maskSensitiveInfo(getRequestBody(requestWrapper));
        String responseBody = getResponseBody(responseWrapper);

        return new ApiLogMessage(
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

    private static String maskSensitiveInfo(String content) {
        try {
            JsonNode jsonNode = objectMapper.readTree(content);
            if (jsonNode.has("password")) {
                ((ObjectNode) jsonNode).put("password", "*****");
            }
            return objectMapper.writeValueAsString(jsonNode);
        } catch (Exception e) {
            // JSON 파싱에 실패한 경우, 간단한 문자열 치환
            return content.replaceAll("\"password\"\\s*:\\s*\"[^\"]*\"", "\"password\":\"*****\"");
        }
    }

    private static String getClientIp(ContentCachingRequestWrapper request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null) {
            return request.getRemoteAddr();
        }
        return xfHeader.split(",")[0];
    }

    public String toJsonLog() {
        try {
            return objectMapper.writeValueAsString(this);
        } catch (Exception e) {
            return "Error creating JSON log: " + e.getMessage();
        }
    }
}
