package io.github.siyoung.csinquirytriage.domain.inquiry.entity;

public enum InquiryCategory {

    REFUND(true),
    SHIPPING_ISSUE(true),
    COMPLAINT(true),
    GENERAL(false);

    private final boolean sensitive;

    InquiryCategory(boolean sensitive) {
        this.sensitive = sensitive;
    }

    public boolean isSensitive() {
        return sensitive;
    }
}
