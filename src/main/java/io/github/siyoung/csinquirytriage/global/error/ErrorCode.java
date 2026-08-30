package io.github.siyoung.csinquirytriage.global.error;

import io.github.siyoung.csinquirytriage.global.exception.CommonException;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    /* Inquiry */
    INQUIRY_NOT_FOUND(HttpStatus.NOT_FOUND, "I001", "존재하지 않는 문의입니다."),
    CLASSIFICATION_PARSE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "I002", "LLM 응답 파싱에 실패했습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;

    ErrorCode(HttpStatus status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }

    public CommonException commonException() {
        return new CommonException(this);
    }
}
