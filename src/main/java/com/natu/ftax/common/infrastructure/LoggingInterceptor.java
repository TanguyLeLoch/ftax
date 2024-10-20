package com.natu.ftax.common.infrastructure;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class LoggingInterceptor implements ClientHttpRequestInterceptor {
    private static final Logger log = LoggerFactory.getLogger(LoggingInterceptor.class);

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body,
                                        ClientHttpRequestExecution execution) throws IOException {
        String requestId = UUID.randomUUID().toString();
        logRequest(requestId, request, body);

        long startTime = System.currentTimeMillis();
        ClientHttpResponse response = execution.execute(request, body);
        long duration = System.currentTimeMillis() - startTime;

        logResponse(requestId, response, duration);

        return response;
    }

    private void logRequest(String requestId, HttpRequest request, byte[] body) {
//        log.info("request id: {}, uri: {}, method: {}", requestId, request.getURI(), request.getMethod());
    }

    private void logResponse(String requestId, ClientHttpResponse response, long duration)
            throws IOException {
        String bodyString = StreamUtils.copyToString(response.getBody(), StandardCharsets.UTF_8);
        if (bodyString.contains("NOTOK")) {
            log.info("request id: {}, status code: {}, duration: {}ms, response headers: {}, " + "response body: {}",
                    requestId, response.getStatusCode(), duration, response.getHeaders(), bodyString);
        }
    }
}