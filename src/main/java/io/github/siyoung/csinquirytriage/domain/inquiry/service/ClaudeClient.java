package io.github.siyoung.csinquirytriage.domain.inquiry.service;

import io.github.siyoung.csinquirytriage.domain.inquiry.dto.ClassificationResult;

public interface ClaudeClient {

    ClassificationResult classify(String inquiryContent);
}
