package io.github.siyoung.csinquirytriage.domain.inquiry.service;

import io.github.siyoung.csinquirytriage.domain.inquiry.entity.Inquiry;
import io.github.siyoung.csinquirytriage.domain.inquiry.entity.InquiryStatus;
import io.github.siyoung.csinquirytriage.domain.inquiry.repository.InquiryRepository;
import io.github.siyoung.csinquirytriage.domain.inquiry.dto.ClassificationResult;
import io.github.siyoung.csinquirytriage.global.error.ErrorCode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class InquiryTriageService {

    private final ClaudeClient claudeClient;
    private final SlackNotifier slackNotifier;
    private final InquiryRepository inquiryRepository;
    private final double autoSendConfidenceThreshold;

    public InquiryTriageService(
            ClaudeClient claudeClient,
            SlackNotifier slackNotifier,
            InquiryRepository inquiryRepository,
            @Value("${triage.auto-send-confidence-threshold}") double autoSendConfidenceThreshold
    ) {
        this.claudeClient = claudeClient;
        this.slackNotifier = slackNotifier;
        this.inquiryRepository = inquiryRepository;
        this.autoSendConfidenceThreshold = autoSendConfidenceThreshold;
    }

    @Transactional
    public Inquiry triage(String content) {
        Inquiry inquiry = new Inquiry(content);

        Optional<ClassificationResult> result = claudeClient.classify(content);

        boolean autoSend = false;
        if (result.isPresent()) {
            ClassificationResult classification = result.get();
            inquiry.applyClassification(classification.category(), classification.confidence(), classification.draftAnswer());
            autoSend = shouldAutoSend(classification);
        }

        if (autoSend) {
            inquiry.markAutoSent();
        } else {
            inquiry.markNeedsReview();
        }

        Inquiry saved = inquiryRepository.save(inquiry);

        if (!autoSend) {
            slackNotifier.notifyNeedsReview(saved.getId(), content);
        }

        return saved;
    }

    public Inquiry getInquiry(Long id) {
        return inquiryRepository.findById(id)
                .orElseThrow(ErrorCode.INQUIRY_NOT_FOUND::commonException);
    }

    public List<Inquiry> getInquiries(InquiryStatus status) {
        return status != null ? inquiryRepository.findByStatus(status) : inquiryRepository.findAll();
    }

    private boolean shouldAutoSend(ClassificationResult result) {
        return !result.category().isSensitive() && result.confidence() >= autoSendConfidenceThreshold;
    }
}
