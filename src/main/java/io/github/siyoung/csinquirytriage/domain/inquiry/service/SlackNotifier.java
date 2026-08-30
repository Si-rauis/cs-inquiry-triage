package io.github.siyoung.csinquirytriage.domain.inquiry.service;

public interface SlackNotifier {

    void notifyNeedsReview(Long inquiryId, String content);
}
