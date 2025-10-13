package com.common.response;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class ResponseUtils {
    public <T> ResponseEntity<ApiResponse<T>> buildSuccess(T data, String message, HttpStatus status) {
        return new ResponseEntity<>(ApiResponse.success(data, message, status.value()), status);
    }

    public <T> ResponseEntity<ApiResponse<T>> buildFailure(T data, String message, HttpStatus status) {
        return new ResponseEntity<>(ApiResponse.error(data, message, status.value()), status);
    }
}
