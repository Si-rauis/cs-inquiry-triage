package io.github.siyoung.csinquirytriage.domain.inquiry.service;

import io.github.siyoung.csinquirytriage.domain.inquiry.constant.SlackMessageConstants;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Map;

@Component
public class SlackWebhookNotifier implements SlackNotifier {

    private final RestClient restClient;

    public SlackWebhookNotifier(@Value("${slack.webhook-url}") String webhookUrl) {
        this.restClient = RestClient.create(webhookUrl);
    }

    @Override
    public void notifyNeedsReview(Long inquiryId, String content) {
        restClient.post()
                .body(Map.of("text", SlackMessageConstants.NEEDS_REVIEW_MESSAGE_FORMAT.formatted(inquiryId, content)))
                .retrieve()
                .toBodilessEntity();
    }
}
