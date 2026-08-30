package io.github.siyoung.csinquirytriage.domain.inquiry.controller;

import io.github.siyoung.csinquirytriage.domain.inquiry.constant.ApiPathConstants;
import io.github.siyoung.csinquirytriage.domain.inquiry.dto.request.InquiryCreateRequest;
import io.github.siyoung.csinquirytriage.domain.inquiry.dto.response.InquiryResponse;
import io.github.siyoung.csinquirytriage.domain.inquiry.entity.Inquiry;
import io.github.siyoung.csinquirytriage.domain.inquiry.entity.InquiryStatus;
import io.github.siyoung.csinquirytriage.domain.inquiry.service.InquiryTriageService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(ApiPathConstants.INQUIRIES)
public class InquiryController {

    private final InquiryTriageService inquiryTriageService;

    public InquiryController(InquiryTriageService inquiryTriageService) {
        this.inquiryTriageService = inquiryTriageService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public InquiryResponse submit(@Valid @RequestBody InquiryCreateRequest request) {
        Inquiry inquiry = inquiryTriageService.triage(request.content());
        return InquiryResponse.from(inquiry);
    }

    @GetMapping("/{id}")
    public InquiryResponse getById(@PathVariable Long id) {
        return InquiryResponse.from(inquiryTriageService.getInquiry(id));
    }

    @GetMapping
    public List<InquiryResponse> list(@RequestParam(required = false) InquiryStatus status) {
        return inquiryTriageService.getInquiries(status).stream()
                .map(InquiryResponse::from)
                .toList();
    }
}
