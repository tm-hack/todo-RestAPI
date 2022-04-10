package com.example.todo.api.common.error;

import javax.inject.Inject;

import org.springframework.context.MessageSource;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class RestGlobalExceptionHandler extends ResponseEntityExceptionHandler {
	
	@Inject
	MessageSource messageSource;
	
	@Override
	protected ResponseEntity<Object> handleExceptionInternal(Exception ex,
			Object body, HttpHeaders headers, HttpStatus status,
			WebRequest request) {
		Object responseBody = body;
		if (body == null) {
			responseBody = createApiError(request, "E999", ex.getMessage());
		}
		return ResponseEntity.status(status).headers(headers).body(responseBody);
	}
	
	private ApiError createApiError (WebRequest request, String errorCode,
			Object... args) {
		return new ApiError(errorCode, messageSource.getMessage(errorCode,
				args, request.getLocale())); 
	}
	
	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(
			MethodArgumentNotValidException ex, HttpHeaders headers,
			HttpStatus status, WebRequest request ){
		ApiError apiError = createApiError(request, "E400");
		
		for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
			apiError.addDetail(createApiError(request, fieldError, fieldError.getField()));
		}
		
		for (ObjectError objectError : ex.getBindingResult().getGlobalErrors()) {
			apiError.addDetail(createApiError(request, objectError, objectError.getObjectName()));			
		}
		
		return handleExceptionInternal(ex, apiError, headers, status, request);
	}
	
	private ApiError createApiError (WebRequest request,
			DefaultMessageSourceResolvable messageSourceResolvable,
			String target) {
		return  new ApiError(messageSourceResolvable.getCode(), messageSource.getMessage(
				messageSourceResolvable, request.getLocale()), target);
	}
	
}
