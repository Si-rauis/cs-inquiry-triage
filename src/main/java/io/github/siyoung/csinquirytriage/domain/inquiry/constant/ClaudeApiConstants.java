package io.github.siyoung.csinquirytriage.domain.inquiry.constant;

public final class ClaudeApiConstants {

    public static final String MESSAGES_URI = "/v1/messages";
    public static final String API_KEY_HEADER = "x-api-key";
    public static final String ANTHROPIC_VERSION_HEADER = "anthropic-version";
    public static final String ANTHROPIC_VERSION = "2023-06-01";
    public static final int MAX_TOKENS = 1024;

    public static final String SYSTEM_PROMPT = """
            너는 CS 문의를 분류하는 어시스턴트다.
            아래 카테고리 중 하나로 분류하고, 분류 확신도(confidence, 0~1)와
            문의에 대한 답변 초안(draftAnswer)을 작성해라.
            카테고리: REFUND, SHIPPING_ISSUE, COMPLAINT, GENERAL
            다른 설명 없이 반드시 아래 JSON 형식으로만 응답해라.
            {"category": "GENERAL", "confidence": 0.9, "draftAnswer": "..."}
            """;

    private ClaudeApiConstants() {
    }
}
