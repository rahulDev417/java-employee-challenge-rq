package com.reliaquest.employee.accessor.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 
 * Employee POJO returned from 3rd party API
 * 
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeData {
	/**
	 * Employee Id
	 */
	@JsonProperty("id")
	private int id;

	/**
	 * Employee Name
	 */
	@JsonProperty("employee_name")
	private String name;

	/**
	 * Employee Salary
	 */
	@JsonProperty("employee_salary")
	private int salary;

	/**
	 * Employee age
	 */
	@JsonProperty("employee_age")
	private int age;

	/**
	 * Employee profile image url
	 */
	@JsonProperty("profile_image")
	private int profileImage;

}
