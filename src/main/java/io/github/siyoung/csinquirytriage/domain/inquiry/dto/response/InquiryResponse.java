package io.github.siyoung.csinquirytriage.domain.inquiry.dto.response;

import io.github.siyoung.csinquirytriage.domain.inquiry.entity.Inquiry;
import io.github.siyoung.csinquirytriage.domain.inquiry.entity.InquiryCategory;
import io.github.siyoung.csinquirytriage.domain.inquiry.entity.InquiryStatus;

public record InquiryResponse(
        Long id,
        String content,
        InquiryCategory category,
        Double confidence,
        String draftAnswer,
        InquiryStatus status
) {
    public static InquiryResponse from(Inquiry inquiry) {
        return new InquiryResponse(
                inquiry.getId(),
                inquiry.getContent(),
                inquiry.getCategory(),
                inquiry.getConfidence(),
                inquiry.getDraftAnswer(),
                inquiry.getStatus()
        );
    }
}
