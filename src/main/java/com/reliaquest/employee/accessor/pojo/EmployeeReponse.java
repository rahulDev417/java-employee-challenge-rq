package com.reliaquest.employee.accessor.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * API Response of Employee API
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeReponse {
	/**
	 * API Status
	 */
	private String status;
	/**
	 * Employee List
	 */
	private EmployeeData data;
	/**
	 * API response message
	 */
	private String message;
}
