package com.nhnacademy.illuwa.advice;

import com.nhnacademy.illuwa.dto.ErrorResponse;
import feign.FeignException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class AuthGlobalExceptionHandler {
    @ExceptionHandler(FeignException.class)
    public ResponseEntity<ErrorResponse> handleFeignGeneric(FeignException ex, HttpServletRequest request) {
        HttpStatus status = HttpStatus.resolve(ex.status());

        String code = switch (status) {
            case NOT_FOUND -> "USER_NOT_FOUND";
            case BAD_REQUEST -> "INVALID_REQUEST";
            case UNAUTHORIZED -> "UNAUTHORIZED";
            default -> "FEIGN_ERROR";
        };

        String message = switch (status) {
            case NOT_FOUND -> "해당 유저를 찾을 수 없습니다.";
            case BAD_REQUEST -> "요청이 올바르지 않습니다.";
            case UNAUTHORIZED -> "인증되지 않은 요청입니다.";
            default -> "내부 서비스 호출 중 오류가 발생했습니다.";
        };

        ErrorResponse error = ErrorResponse.of(
                status != null ? status.value() : 500,
                status != null ? status.getReasonPhrase() : "Internal Server Error",
                code,
                message,
                request.getRequestURI()
        );

        return ResponseEntity.status(status != null ? status : HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}
