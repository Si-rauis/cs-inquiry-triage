package io.github.siyoung.csinquirytriage.domain.inquiry.service;

import io.github.siyoung.csinquirytriage.domain.inquiry.dto.ClassificationResult;

import java.util.Optional;

public interface ClaudeClient {

    Optional<ClassificationResult> classify(String inquiryContent);
}
