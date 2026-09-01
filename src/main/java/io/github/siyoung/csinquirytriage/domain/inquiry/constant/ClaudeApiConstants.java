package io.github.siyoung.csinquirytriage.domain.inquiry.constant;

import java.util.List;
import java.util.Map;

public final class ClaudeApiConstants {

    public static final String MESSAGES_URI = "/v1/messages";
    public static final String API_KEY_HEADER = "x-api-key";
    public static final String ANTHROPIC_VERSION_HEADER = "anthropic-version";
    public static final String ANTHROPIC_VERSION = "2023-06-01";
    public static final int MAX_TOKENS = 1024;
    public static final int MAX_CLASSIFY_ATTEMPTS = 3;

    public static final String SYSTEM_PROMPT = """
            너는 CS 문의를 분류하는 어시스턴트다.
            문의 내용을 보고 카테고리(REFUND, SHIPPING_ISSUE, COMPLAINT, GENERAL) 중 하나로 분류하고,
            분류에 대한 확신도(confidence, 0~1)와 문의에 대한 답변 초안(draftAnswer)을 작성해서
            classify_inquiry 도구를 호출해라.
            """;

    public static final String CLASSIFY_TOOL_NAME = "classify_inquiry";

    public static final Map<String, Object> CLASSIFY_TOOL = Map.of(
            "name", CLASSIFY_TOOL_NAME,
            "description", "CS 문의를 카테고리로 분류하고 확신도와 답변 초안을 생성한다.",
            "input_schema", Map.of(
                    "type", "object",
                    "properties", Map.of(
                            "category", Map.of(
                                    "type", "string",
                                    "enum", List.of("REFUND", "SHIPPING_ISSUE", "COMPLAINT", "GENERAL")
                            ),
                            "confidence", Map.of(
                                    "type", "number",
                                    "description", "분류 확신도, 0~1 사이 값"
                            ),
                            "draftAnswer", Map.of(
                                    "type", "string",
                                    "description", "문의에 대한 답변 초안"
                            )
                    ),
                    "required", List.of("category", "confidence", "draftAnswer")
            )
    );

    private ClaudeApiConstants() {
    }
}
