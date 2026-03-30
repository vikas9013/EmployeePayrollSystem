package com.vikas.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.Random;

@Slf4j
@Service
public class AIOnboardingService {

    @Value("${groq.api.key}")
    private String apiKey;

    private final WebClient webClient = WebClient.builder()
            .baseUrl("https://api.groq.com")
            .build();

    // Different tones so even same designation gets a different style message each time
    private static final List<String> TONES = List.of(
            "warm and encouraging",
            "enthusiastic and energetic",
            "professional and inspiring",
            "friendly and motivating",
            "sincere and heartfelt",
            "upbeat and cheerful",
            "thoughtful and supportive"
    );

    // Designation-specific context so the message feels tailored to the actual role
    private String getDesignationContext(String designation) {
        if (designation == null) return "";

        return switch (designation.toLowerCase()) {

            case "software engineer", "developer", "sde", "engineer" ->
                    "They will be building and shipping features, writing clean code, " +
                            "solving complex technical problems, and collaborating with the engineering team.";

            case "senior software engineer", "senior developer", "senior sde" ->
                    "They will be leading technical decisions, mentoring junior developers, " +
                            "designing scalable systems, and driving engineering excellence.";

            case "manager", "team lead", "engineering manager" ->
                    "They will be leading a team, driving project delivery, " +
                            "mentoring team members, and aligning the team with company goals.";

            case "hr", "human resources", "hr manager", "recruiter" ->
                    "They will be building the team, nurturing company culture, " +
                            "supporting employee growth, and making the workplace a great place to be.";

            case "intern", "trainee" ->
                    "They are starting their professional journey, eager to learn, " +
                            "grow their skills, and contribute meaningfully to the team.";

            case "product manager", "pm" ->
                    "They will be defining the product vision, working closely with " +
                            "engineering and design, and delivering real value to customers.";

            case "designer", "ui designer", "ux designer" ->
                    "They will be crafting beautiful user experiences, collaborating with " +
                            "product and engineering, and bringing creativity to every pixel.";

            case "devops", "devops engineer", "sre" ->
                    "They will be building and maintaining our infrastructure, ensuring " +
                            "reliability, automating deployments, and keeping systems running smoothly.";

            case "data scientist", "data analyst", "ml engineer" ->
                    "They will be turning data into insights, building intelligent models, " +
                            "and helping the company make smarter data-driven decisions.";

            case "consultant" ->
                    "They will be delivering expert guidance to clients, solving business " +
                            "challenges, and building strong long-term client relationships.";

            default ->
                    "They will be bringing their unique skills and perspective to the team, " +
                            "contributing to our shared goals and company success.";
        };
    }

    public String generateMessage(String name, String designation, String department) {

        log.info("[AIOnboardingService] Generating welcome message for: {} ({})", name, designation);

        // Pick a random tone — this ensures even same designation gets a different
        // style message every time (warm vs enthusiastic vs professional etc.)
        String tone = TONES.get(new Random().nextInt(TONES.size()));

        // Get role-specific context based on designation
        String roleContext = getDesignationContext(designation);

        String dept = (department != null && !department.isBlank()) ? department : "the company";

        // Prompt is specific to: name + designation + role context + random tone
        // Result: every message is unique even for employees with the same designation
        String prompt =
                "Write a short, " + tone + " onboarding welcome message for " +
                        name + " who is joining as " + designation + " at " + dept + ". " +
                        "About their role: " + roleContext + " " +
                        "The message must mention their name (" + name + ") and their specific " +
                        "role (" + designation + ") naturally. " +
                        "Make it personal, genuine, and specific to what they will actually be doing — " +
                        "NOT a generic welcome message. " +
                        "Keep it under 80 words. Do not use bullet points.";

        Map<String, Object> requestBody = Map.of(
                "model", "llama-3.3-70b-versatile",
                "messages", List.of(Map.of("role", "user", "content", prompt)),
                "max_tokens", 256,
                // temperature 0.9 = more creative and varied output each time
                // Lower = more predictable, Higher = more creative/random
                "temperature", 0.9
        );

        try {
            Map<?, ?> response = webClient.post()
                    .uri("/openai/v1/chat/completions")
                    .header("Authorization", "Bearer " + apiKey)
                    .header("Content-Type", "application/json")
                    .bodyValue(requestBody)
                    .retrieve()
                    .onStatus(status -> status.is4xxClientError(), clientResponse ->
                            clientResponse.bodyToMono(String.class)
                                    .doOnNext(body -> log.error("[AIOnboardingService] Groq 4xx: {}", body))
                                    .then(Mono.error(new RuntimeException("Groq API 4xx error")))
                    )
                    .onStatus(status -> status.is5xxServerError(), clientResponse ->
                            clientResponse.bodyToMono(String.class)
                                    .doOnNext(body -> log.error("[AIOnboardingService] Groq 5xx: {}", body))
                                    .then(Mono.error(new RuntimeException("Groq API 5xx error")))
                    )
                    .bodyToMono(Map.class)
                    .block();

            if (response != null && response.containsKey("choices")) {
                List<?> choices = (List<?>) response.get("choices");
                if (choices != null && !choices.isEmpty()) {
                    Map<?, ?> firstChoice = (Map<?, ?>) choices.get(0);
                    Map<?, ?> message     = (Map<?, ?>) firstChoice.get("message");
                    if (message != null && message.get("content") != null) {
                        String result = message.get("content").toString();
                        log.info("[AIOnboardingService] Message generated for: {} ({})",
                                name, designation);
                        return result;
                    }
                }
            }

        } catch (Exception ex) {
            log.warn("[AIOnboardingService] Groq call failed, using fallback. Reason: {}",
                    ex.getMessage());
        }

        // Fallback also uses designation — not completely generic
        return "Welcome to the team, " + name + "! We are thrilled to have you join us as "
                + designation + ". Your skills and passion will be a fantastic addition. "
                + "We cannot wait to see the impact you will make!";
    }
}