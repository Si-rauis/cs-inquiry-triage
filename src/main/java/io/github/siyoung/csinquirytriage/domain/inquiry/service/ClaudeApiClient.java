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
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Map;

@Component
public class ClaudeApiClient implements ClaudeClient {

    private static final Logger log = LoggerFactory.getLogger(ClaudeApiClient.class);

    private final RestClient restClient;
    private final ObjectMapper objectMapper;
    private final String model;

    public ClaudeApiClient(
            @Value("${claude.base-url}") String baseUrl,
            @Value("${claude.api-key}") String apiKey,
            @Value("${claude.model}") String model,
            ObjectMapper objectMapper
    ) {
        this.model = model;
        this.objectMapper = objectMapper;
        this.restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader(ClaudeApiConstants.API_KEY_HEADER, apiKey)
                .defaultHeader(ClaudeApiConstants.ANTHROPIC_VERSION_HEADER, ClaudeApiConstants.ANTHROPIC_VERSION)
                .build();
    }

    @Override
    public ClassificationResult classify(String inquiryContent) {
        Map<String, Object> requestBody = Map.of(
                "model", model,
                "max_tokens", ClaudeApiConstants.MAX_TOKENS,
                "system", ClaudeApiConstants.SYSTEM_PROMPT,
                "messages", List.of(Map.of("role", "user", "content", inquiryContent))
        );

        JsonNode response = restClient.post()
                .uri(ClaudeApiConstants.MESSAGES_URI)
                .body(requestBody)
                .retrieve()
                .body(JsonNode.class);

        String text = response.path("content").path(0).path("text").asString();
        if (text.isEmpty()) {
            log.warn("Claude 응답에서 text 추출 실패, 전체 응답={}", response);
        }
        return parse(text);
    }

    private ClassificationResult parse(String text) {
        try {
            JsonNode json = objectMapper.readTree(text);
            InquiryCategory category = InquiryCategory.valueOf(json.get("category").asString());
            double confidence = json.get("confidence").asDouble();
            String draftAnswer = json.get("draftAnswer").asString();
            return new ClassificationResult(category, confidence, draftAnswer);
        } catch (Exception e) {
            log.warn("Claude 응답 파싱 실패: {} / raw={}", e.getMessage(), text);
            throw ErrorCode.CLASSIFICATION_PARSE_FAILED.commonException();
        }
    }
}
