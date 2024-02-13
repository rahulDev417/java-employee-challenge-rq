package com.reliaquest.employee.exception;

/**
 * Exception for 3rd party failure
 */
public class EmployeeAccessorException extends RuntimeException {

	public EmployeeAccessorException() {
		// TODO Auto-generated constructor stub
	}

	public EmployeeAccessorException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	public EmployeeAccessorException(EmployeeAccessorError error) {
		super(error.getMessage());
		// TODO Auto-generated constructor stub
	}

	public EmployeeAccessorException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

	public EmployeeAccessorException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	public EmployeeAccessorException(EmployeeAccessorError error, Throwable cause) {
		super(error.getMessage(), cause);
		// TODO Auto-generated constructor stub
	}

	public EmployeeAccessorException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		// TODO Auto-generated constructor stub
	}

}
