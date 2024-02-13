package com.reliaquest.employee.accessor.pojo;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request Enity for 3rd party API
 */
@Data
@NoArgsConstructor
public class EmployeeRequest {
	/**
	 * Name of the employee
	 */
	private String name;
	/**
	 * Age of the Employee
	 */
	private int age;
	/**
	 * Salary of the Employee
	 */
	private int salary;
}
