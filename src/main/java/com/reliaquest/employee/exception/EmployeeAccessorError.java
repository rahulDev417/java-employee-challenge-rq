package com.reliaquest.employee.exception;

import java.util.Arrays;

public enum EmployeeAccessorError {
	SERVICE_ERROR("Error receieved from 3rd Party API."),
	JSON_PARSSING_ERROR("Expected Response mismatch"),
	API_RESOURCE_NOT_FOUND("API Resource Not found"),
	SERVICE_NETWORK_ERROR("Netwrok error while fetching data.");
	private String message;

	private EmployeeAccessorError(String message) {
		this.message = message;
	}

	public String getMessage() {
		return message;
	}
	public static EmployeeAccessorError findByValue(final String value){
		return Arrays.stream(values()).filter(t -> t.getMessage().equals(value)).findFirst().orElse(null);
	}
	

}
