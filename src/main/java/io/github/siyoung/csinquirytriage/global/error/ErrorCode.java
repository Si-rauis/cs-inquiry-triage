package io.github.siyoung.csinquirytriage.global.error;

import io.github.siyoung.csinquirytriage.global.exception.CommonException;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    /* Inquiry */
    INQUIRY_NOT_FOUND(HttpStatus.NOT_FOUND, "I001", "존재하지 않는 문의입니다."),
    CLASSIFICATION_TOOL_INPUT_NOT_FOUND(HttpStatus.INTERNAL_SERVER_ERROR, "I002", "Claude 응답에서 분류 결과를 찾을 수 없습니다.");

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
