package io.github.siyoung.csinquirytriage.domain.inquiry.dto;

import io.github.siyoung.csinquirytriage.domain.inquiry.entity.InquiryCategory;

public record ClassificationResult(
        InquiryCategory category,
        double confidence,
        String draftAnswer
) {
}
