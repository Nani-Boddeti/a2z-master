package com.a2z.controllers;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.a2z.validators.ValidationErrorResponse;
import com.a2z.validators.Violation;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;

@ControllerAdvice
public class ErrorHandlingControllerAdvice {
	@ExceptionHandler(ConstraintViolationException.class)
	  @ResponseStatus(HttpStatus.BAD_REQUEST)
	  @ResponseBody
	  ValidationErrorResponse onConstraintValidationException(
	      ConstraintViolationException e) {
	    ValidationErrorResponse error = new ValidationErrorResponse();
	    for (ConstraintViolation violation : e.getConstraintViolations()) {
	    	List<Violation> violationsList = new ArrayList<>();
	    	violationsList.addAll(error.getViolations());
	    	violationsList.add(new Violation(violation.getPropertyPath().toString(), violation.getMessage()));
	      error.setViolations(violationsList);
	    }
	    return error;
	  }
	
	@ExceptionHandler(MethodArgumentNotValidException.class)
	  @ResponseStatus(HttpStatus.BAD_REQUEST)
	  @ResponseBody
	  ValidationErrorResponse onMethodArgumentNotValidException(
	      MethodArgumentNotValidException e) {
	    ValidationErrorResponse error = new ValidationErrorResponse();
	    for (FieldError fieldError : e.getBindingResult().getFieldErrors()) {
	      error.getViolations().add(
	        new Violation(fieldError.getField(), fieldError.getDefaultMessage()));
	    }
	    return error;
	  }
}
