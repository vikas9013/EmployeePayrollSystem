package com.vikas.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@Service
public class AIOnboardingService {

    @Value("${groq.api.key}")
    private String apiKey;

    private final WebClient webClient = WebClient.builder()
            .baseUrl("https://api.groq.com")
            .build();

    public String generateMessage(String name, String designation, String department) {

        String dept = (department != null && !department.isBlank()) ? department : "the company";

        String prompt = "Generate a short, warm, and professional onboarding welcome message for "
                + name + " who is joining as " + designation + " in the " + dept + " department. "
                + "Keep it under 100 words, friendly, and motivating.";

        Map<String, Object> requestBody = Map.of(
                "model", "llama-3.3-70b-versatile",
                "messages", List.of(
                        Map.of("role", "user", "content", prompt)
                ),
                "max_tokens", 256
        );

        Map<?, ?> response = webClient.post()
                .uri("/openai/v1/chat/completions")
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .bodyValue(requestBody)
                .retrieve()
                .onStatus(status -> status.is4xxClientError(), clientResponse ->
                        clientResponse.bodyToMono(String.class)
                                .doOnNext(body -> System.out.println("[AIOnboardingService] Groq error: " + body))
                                .then(Mono.error(new RuntimeException("Groq API error")))
                )
                .bodyToMono(Map.class)
                .block();

        // Parse: response.choices[0].message.content
        if (response != null && response.containsKey("choices")) {
            List<?> choices = (List<?>) response.get("choices");
            if (choices != null && !choices.isEmpty()) {
                Map<?, ?> firstChoice = (Map<?, ?>) choices.get(0);
                Map<?, ?> message = (Map<?, ?>) firstChoice.get("message");
                if (message != null) {
                    Object content = message.get("content");
                    if (content != null) {
                        return content.toString();
                    }
                }
            }
        }

        return "Welcome to the team, " + name + "! We're thrilled to have you with us.";
    }
}