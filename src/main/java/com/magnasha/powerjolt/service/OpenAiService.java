package com.magnasha.powerjolt.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.Serializable;
import java.util.Map;

@Service
public class OpenAiService {
    @Value("${openai.api.key}")
    private String openaiApiKey;

    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    public OpenAiService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("https://api.openai.com/v1").build();
        this.objectMapper = new ObjectMapper();
    }

    public Mono<Map<String, Object>> generateSpec(String inputJson, String outputJson) {
        String prompt = generatePrompt(inputJson, outputJson);
        return callOpenAiApi(prompt)
                .flatMap(response -> {
                    try {
                        Map<String, Object> joltSpec = parseResponse(response);
                        return Mono.just(joltSpec);
                    } catch (Exception e) {
                        return Mono.error(e);
                    }
                });
    }

    private String generatePrompt(String inputJson, String outputJson) {
        return "Given the input JSON: " + inputJson + " and the desired output JSON: " + outputJson + ", generate the JOLT specification.";
    }

    private Mono<String> callOpenAiApi(String prompt) {
        Map<String, Serializable> requestBody = Map.of(
                "model", "gpt-3.5-turbo-16k",
                "messages", new Object[]{
                        Map.of("role", "system", "content", "You are a helpful assistant."),
                        Map.of("role", "user", "content", prompt)
                }
        );

        ObjectMapper objectMapper = new ObjectMapper();
        String jsonString = null;
        try {
            jsonString = objectMapper.writeValueAsString(requestBody);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        System.out.println(jsonString);



        return webClient.post()
                .uri("/completions")
                .header("Authorization", "Bearer " + openaiApiKey)
                .bodyValue(Map.of(
                        "model", "gpt-4",
                        "messages", new Object[]{
                                Map.of("role", "system", "content", "You are a helpful assistant."),
                                Map.of("role", "user", "content", prompt)
                        }
                ))
                .retrieve()
                .bodyToMono(String.class);
    }

    private Map<String, Object> parseResponse(String response) throws Exception {
        Map<String, Object> parsedResponse = objectMapper.readValue(response, Map.class);
        String joltSpecString = (String) ((Map<?, ?>) ((Map<?, ?>) parsedResponse.get("choices")).get(0)).get("text");
        return objectMapper.readValue(joltSpecString, Map.class);
    }
}
