package com.a2z.controllers;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@Validated
public class RootController {
	
	public static boolean isSessionValid() {
		SecurityContext context = SecurityContextHolder.getContext();
		if(context != null) {
	        Authentication authentication =  context.getAuthentication();
	        if(null != authentication && authentication.isAuthenticated() != true) 
	            return false;
	        else 
	            return true;
	    } else {
	        return false;
	    }
	}
	
	@ExceptionHandler(ConstraintViolationException.class)
	  @ResponseStatus(HttpStatus.BAD_REQUEST)
	  ResponseEntity<String> handleConstraintViolationException(ConstraintViolationException e) {
	    return new ResponseEntity<>("not valid due to validation error: " + e.getMessage(), HttpStatus.BAD_REQUEST);
	  }
	
	public static String getSessionUserName() {
		SecurityContext context = SecurityContextHolder.getContext();
		if(context != null) {
			Authentication authentication =  context.getAuthentication();
	        if(null != authentication && authentication.isAuthenticated() == true) {
	        	return authentication.getName();
	        }
		}
		return StringUtils.EMPTY;
	}
}
