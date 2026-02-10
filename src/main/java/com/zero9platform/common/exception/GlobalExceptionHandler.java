package com.zero9platform.common.exception;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.zero9platform.common.enums.ExceptionCode;
import com.zero9platform.common.model.CommonResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 커스텀 예외 처리
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<CommonResponse<Void>> customException(CustomException e) {

        ExceptionCode exceptionCode = e.getExceptionCode();

        CommonResponse<Void> response = new CommonResponse<>(false, e.getMessage(), null);

        return ResponseEntity.status(exceptionCode.getStatus()).body(response);
    }

    // Valid 검증 예외 처리
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<CommonResponse<Void>> methodArgumentNotValidException(MethodArgumentNotValidException e) {

        String message = e.getBindingResult().getAllErrors().get(0).getDefaultMessage();

        CommonResponse<Void> response = new CommonResponse<>(false, message, null);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    // 요청 Json 검증 예외 처리
    @ExceptionHandler(HttpMessageNotReadableException.class) // RequestBody(JSON)를 읽다가 실패했을 때 발생하는 예외
    public ResponseEntity<CommonResponse<Void>> handleHttpMessageNotReadable(HttpMessageNotReadableException e) {

        Throwable cause = e.getCause(); // HttpMessageNotReadableException의 cause에 원인이 들어있음

        // cause가 InvalidFormatException일 경우
        if (cause instanceof InvalidFormatException invalidFormatException) {

            // 예외 원인의 대상 타입이 Enum일 경우
            if (invalidFormatException.getTargetType().isEnum()) {

                // 예외발생 위치(path) 추적 -> path목록의 첫 요소의 필드명 추출
                String fieldName = invalidFormatException.getPath().get(0).getFieldName();

                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(CommonResponse.fail(fieldName + " 값이 올바르지 않습니다."));
            }
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(CommonResponse.fail("요청 형식(JSON)이 올바르지 않습니다."));
    }

//    // 기간별 랭킹조회 날짜 형식 예외 처리
//    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
//    public ResponseEntity<CommonResponse<Void>> handleDateFormatError() {
//        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(CommonResponse.fail("날짜 형식이 올바르지 않습니다. (yyyy-MM-dd)"));
//    }
}
