package com.a2z.validators;

public class Violation {

	  private  String fieldName;

	  private  String message;

	public Violation(String field, String message) {
		this.fieldName = field;
		this.message = message;
	}

	public String getFieldName() {
		return fieldName;
	}

	public String getMessage() {
		return message;
	}

	  
	}