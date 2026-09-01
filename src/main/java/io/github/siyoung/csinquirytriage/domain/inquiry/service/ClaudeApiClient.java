package io.github.siyoung.csinquirytriage.domain.inquiry.service;

import io.github.siyoung.csinquirytriage.domain.inquiry.constant.ClaudeApiConstants;
import io.github.siyoung.csinquirytriage.domain.inquiry.dto.ClassificationResult;
import io.github.siyoung.csinquirytriage.domain.inquiry.entity.InquiryCategory;
import io.github.siyoung.csinquirytriage.global.error.ErrorCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import tools.jackson.databind.JsonNode;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
public class ClaudeApiClient implements ClaudeClient {

    private static final Logger log = LoggerFactory.getLogger(ClaudeApiClient.class);

    private final RestClient restClient;
    private final String model;

    public ClaudeApiClient(
            @Value("${claude.base-url}") String baseUrl,
            @Value("${claude.api-key}") String apiKey,
            @Value("${claude.model}") String model
    ) {
        this.model = model;
        this.restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader(ClaudeApiConstants.API_KEY_HEADER, apiKey)
                .defaultHeader(ClaudeApiConstants.ANTHROPIC_VERSION_HEADER, ClaudeApiConstants.ANTHROPIC_VERSION)
                .build();
    }

    @Override
    public Optional<ClassificationResult> classify(String inquiryContent) {
        for (int attempt = 1; attempt <= ClaudeApiConstants.MAX_CLASSIFY_ATTEMPTS; attempt++) {
            try {
                return Optional.of(requestClassification(inquiryContent));
            } catch (Exception e) {
                log.warn("Claude 분류 요청 실패 ({}/{}회): {}",
                        attempt, ClaudeApiConstants.MAX_CLASSIFY_ATTEMPTS, e.getMessage());
            }
        }
        return Optional.empty();
    }

    private ClassificationResult requestClassification(String inquiryContent) {
        Map<String, Object> requestBody = Map.of(
                "model", model,
                "max_tokens", ClaudeApiConstants.MAX_TOKENS,
                "system", ClaudeApiConstants.SYSTEM_PROMPT,
                "messages", List.of(Map.of("role", "user", "content", inquiryContent)),
                "tools", List.of(ClaudeApiConstants.CLASSIFY_TOOL),
                "tool_choice", Map.of("type", "tool", "name", ClaudeApiConstants.CLASSIFY_TOOL_NAME)
        );

        JsonNode response = restClient.post()
                .uri(ClaudeApiConstants.MESSAGES_URI)
                .body(requestBody)
                .retrieve()
                .body(JsonNode.class);

        JsonNode input = findToolInput(response);
        InquiryCategory category = InquiryCategory.valueOf(input.get("category").asString());
        double confidence = input.get("confidence").asDouble();
        String draftAnswer = input.get("draftAnswer").asString();
        return new ClassificationResult(category, confidence, draftAnswer);
    }

    private JsonNode findToolInput(JsonNode response) {
        for (JsonNode block : response.path("content")) {
            if (ClaudeApiConstants.CLASSIFY_TOOL_NAME.equals(block.path("name").asString())) {
                return block.path("input");
            }
        }
        throw ErrorCode.CLASSIFICATION_TOOL_INPUT_NOT_FOUND.commonException();
    }
}
