package com.warehouse.inventory.exceptionhandler;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.warehouse.inventory.responce.ApiResponse;

@RestControllerAdvice
public class GlobleExceptionHandler {

	@ExceptionHandler(ResourceNotFoundException.class)
	public ApiResponse<?> handleResourceNotFound(ResourceNotFoundException ex) {
		return ApiResponse.error(ex.getMessage(), ex.getCause(), HttpStatus.NOT_FOUND.value());
	}
}
