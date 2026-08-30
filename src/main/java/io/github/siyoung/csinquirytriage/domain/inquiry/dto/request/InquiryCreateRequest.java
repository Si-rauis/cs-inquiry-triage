package io.github.siyoung.csinquirytriage.domain.inquiry.dto.request;

import jakarta.validation.constraints.NotBlank;

public record InquiryCreateRequest(
        @NotBlank String content
) {
}
