package com.remal.signaltrading.exchange.coinbase.api.exchange;

import static java.util.Collections.emptyList;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.remal.signaltrading.exchange.coinbase.security.Signature;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

/**
 * This class acts as a central point for providing user configuration and making GET/POST/PUT requests as well as
 * getting responses as Lists of objects rather than arrays.
 */
@Slf4j
public class CoinbaseExchangeImpl implements CoinbaseExchange {

    private static final int CONNECTION_TIMEOUT = 5000;

    private final String publicKey;
    private final String passphrase;
    private final String baseUrl;
    private final Signature signature;
    private final ObjectMapper objectMapper;
    private final RestTemplate restTemplate;

    /**
     * Constructor.
     *
     * @param publicKey your coinbase API public key
     * @param passphrase your coinbase API passphrase
     * @param baseUrl coinbase API base URL
     * @param signature request content signature generator
     * @param objectMapper JSON serializer
     */
    public CoinbaseExchangeImpl(String publicKey,
                                String passphrase,
                                String baseUrl,
                                Signature signature,
                                ObjectMapper objectMapper) {

        this.publicKey = publicKey;
        this.passphrase = passphrase;
        this.baseUrl = baseUrl;
        this.signature = signature;
        this.objectMapper = objectMapper;

        this.restTemplate = new RestTemplate();
        ((SimpleClientHttpRequestFactory)restTemplate.getRequestFactory()).setConnectTimeout(CONNECTION_TIMEOUT);
        ((SimpleClientHttpRequestFactory)restTemplate.getRequestFactory()).setReadTimeout(CONNECTION_TIMEOUT);
    }

    @Override
    public <T> T get(String resourcePath, ParameterizedTypeReference<T> responseType) {
        try {
            ResponseEntity<T> responseEntity = restTemplate.exchange(getBaseUrl() + resourcePath,
                    HttpMethod.GET,
                    securityHeaders(resourcePath,
                    "GET",
                     ""),
                    responseType);
            return responseEntity.getBody();
        } catch (HttpClientErrorException ex) {
            log.warn("GET request failed for '{}': {}", resourcePath, ex.getMessage());
        }
        return null;
    }

    @Override
    public <T> List<T> getAsList(String resourcePath, ParameterizedTypeReference<T[]> responseType) {
        T[] result = get(resourcePath, responseType);
        return result == null ? emptyList() : Arrays.asList(result);
    }

    @Override
    public <T> T pagedGet(String resourcePath,
                          ParameterizedTypeReference<T> responseType,
                          String beforeOrAfter,
                          Integer pageNumber,
                          Integer limit) {
        resourcePath += "?" + beforeOrAfter + "=" + pageNumber + "&limit=" + limit;
        return get(resourcePath, responseType);
    }

    @Override
    public <T> List<T> pagedGetAsList(String resourcePath,
                          ParameterizedTypeReference<T[]> responseType,
                          String beforeOrAfter,
                          Integer pageNumber,
                          Integer limit) {
        T[] result = pagedGet(resourcePath, responseType, beforeOrAfter, pageNumber, limit);
        return result == null ? emptyList() : Arrays.asList(result);
    }

    @Override
    public <T> T delete(String resourcePath, ParameterizedTypeReference<T> responseType) {
        try {
            ResponseEntity<T> response = restTemplate.exchange(getBaseUrl() + resourcePath,
                HttpMethod.DELETE,
                securityHeaders(resourcePath, "DELETE", ""),
                responseType);
            return response.getBody();
        } catch (HttpClientErrorException ex) {
            log.error("DELETE request failed for '{}': {}", resourcePath, ex.getResponseBodyAsString());
        }
        return null;
    }

    @Override
    public <T, R> T post(String resourcePath, ParameterizedTypeReference<T> responseType, R jsonObj) {
        String jsonBody = toJson(jsonObj);

        try {
            ResponseEntity<T> response = restTemplate.exchange(getBaseUrl() + resourcePath,
                    HttpMethod.POST,
                    securityHeaders(resourcePath, "POST", jsonBody),
                    responseType);
            return response.getBody();
        } catch (HttpClientErrorException ex) {
            log.error("POST request failed for '{}': {}", resourcePath, ex.getResponseBodyAsString());
        }
        return null;
    }

    @Override
    public String getBaseUrl() {
        return baseUrl;
    }

    @Override
    public HttpEntity<String> securityHeaders(String endpoint, String method, String jsonBody) {
        HttpHeaders headers = new HttpHeaders();

        String timestamp = Instant.now().getEpochSecond() + "";
        String resource = endpoint.replace(getBaseUrl(), "");

        headers.add("accept", "application/json");
        headers.add("content-type", "application/json");
        headers.add("User-Agent", "gdax-java unofficial coinbase pro api library");
        headers.add("CB-ACCESS-KEY", publicKey);
        headers.add("CB-ACCESS-SIGN", signature.generate(resource, method, jsonBody, timestamp));
        headers.add("CB-ACCESS-TIMESTAMP", timestamp);
        headers.add("CB-ACCESS-PASSPHRASE", passphrase);

        curlRequest(method, jsonBody, headers, resource);

        return new HttpEntity<>(jsonBody, headers);
    }

    /**
     * Purely here for logging an equivalent curl request for debugging
     * note that the signature is time-sensitive and has a time to live of about 1 minute after which the request
     * is no longer valid.
     */
    private void curlRequest(String method, String jsonBody, HttpHeaders headers, String resource) {
        StringBuilder curlTest = new StringBuilder().append("curl ");
        for (String key : headers.keySet()) {
            curlTest.append("-H '").append(key).append(":").append(headers.get(key).get(0)).append("' ");
        }
        if (jsonBody != null && !jsonBody.equals("")) {
            curlTest.append("-d '").append(jsonBody).append("' ");
        }

        curlTest.append("-X ").append(method).append(" ").append(getBaseUrl()).append(resource);
        log.debug(curlTest.toString());
    }

    private String toJson(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            log.error("Unable to serialize", e);
            throw new RuntimeException("Unable to serialize");
        }
    }
}
