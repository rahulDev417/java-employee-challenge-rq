package com.reliaquest.employee.accessor.pojo;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * API Response of Employee API
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ListEmployeeReponse {
	/**
	 * API Status
	 */
	private String status;
	/**
	 * Employee List
	 */
	private List<EmployeeData> data;
	/**
	 * API response message
	 */
	private String message;
}
