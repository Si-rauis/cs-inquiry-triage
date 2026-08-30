package io.github.siyoung.csinquirytriage.domain.inquiry.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Inquiry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Enumerated(EnumType.STRING)
    private InquiryCategory category;

    private Double confidence;

    @Column(columnDefinition = "TEXT")
    private String draftAnswer;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InquiryStatus status;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public Inquiry(String content) {
        this.content = content;
        this.status = InquiryStatus.RECEIVED;
        this.createdAt = LocalDateTime.now();
    }

    public void applyClassification(InquiryCategory category, double confidence, String draftAnswer) {
        this.category = category;
        this.confidence = confidence;
        this.draftAnswer = draftAnswer;
    }

    public void markAutoSent() {
        this.status = InquiryStatus.AUTO_SENT;
    }

    public void markNeedsReview() {
        this.status = InquiryStatus.NEEDS_REVIEW;
    }
}
