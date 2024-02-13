package com.reliaquest.employee.exception.handler;

import java.util.Objects;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.reliaquest.employee.exception.EmployeeAccessorError;
import com.reliaquest.employee.exception.EmployeeAccessorException;
import com.reliaquest.employee.exception.EmployeeNotFoundException;
import com.reliaquest.employee.model.ErrorDTO;

@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

	@ExceptionHandler(EmployeeAccessorException.class)
	public ResponseEntity<ErrorDTO> handleEmployeeAccessorException(EmployeeAccessorException exception) {
		ErrorDTO errorDTO;
		EmployeeAccessorError error = EmployeeAccessorError.findByValue(exception.getMessage());
		if (Objects.isNull(error)) {
			errorDTO = new ErrorDTO(HttpStatus.INTERNAL_SERVER_ERROR.value(), "EMPLOYEE_SERVICE_API_ERROR",
					"EMPLOYEE_SERVICE_API_ERROR");
		} else {
			errorDTO = new ErrorDTO(HttpStatus.INTERNAL_SERVER_ERROR.value(), error.name(), error.getMessage());
		}
		return new ResponseEntity<>(errorDTO, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ExceptionHandler(EmployeeNotFoundException.class)
	public ResponseEntity<ErrorDTO> handleEmployeeNotFound(EmployeeNotFoundException exception) {
		final ErrorDTO errorDTO = new ErrorDTO(HttpStatus.NOT_FOUND.value(), "EMPLOYEE_NOT_FOUND",
				"EMPLOYEE_NOT_FOUND");
		return new ResponseEntity<>(errorDTO, HttpStatus.NOT_FOUND);
	}

}
