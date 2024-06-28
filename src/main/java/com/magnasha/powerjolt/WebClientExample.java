package com.magnasha.powerjolt;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

public class WebClientExample {

    public static void main(String[] args) {


        WebClient webClient = WebClient.create("http://localhost:8080");

        Map<String, String> requestBody = Map.of(
                "input", "{\"inputKey\": \"value\"}",
                "output", "{\"outputKey\": \"value\"}"
        );

        Mono<Map> response = webClient.post()
                .uri("/api/generate-spec")
                .bodyValue(requestBody)
                .header(HttpHeaders.AUTHORIZATION, basicAuth("user", "password"))
                .header("x-api-key","testapikey")
                .retrieve()
                .bodyToMono(Map.class);

        response.subscribe(res -> {
            System.out.println("Response: " + res);
        });

        // Subscribe to the response and handle it asynchronously
        response.subscribe(
                responseBody -> System.out.println("Response Body: " + responseBody),
                error -> System.err.println("Error: " + error.getMessage()),
                () -> System.out.println("Request completed successfully")
        );

        // Block until the request completes (for demonstration purposes only, not recommended in production)
        response.block();
    }

    private static String basicAuth(String username, String password) {
        String auth = username + ":" + password;
        byte[] encodedAuth = java.util.Base64.getEncoder().encode(auth.getBytes());
        return "Basic " + new String(encodedAuth);
    }
}
