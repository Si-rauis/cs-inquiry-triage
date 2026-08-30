package io.github.siyoung.csinquirytriage.domain.inquiry.repository;

import io.github.siyoung.csinquirytriage.domain.inquiry.entity.Inquiry;
import io.github.siyoung.csinquirytriage.domain.inquiry.entity.InquiryStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InquiryRepository extends JpaRepository<Inquiry, Long> {

    List<Inquiry> findByStatus(InquiryStatus status);
}
