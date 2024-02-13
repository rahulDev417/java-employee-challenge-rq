/**
 * 
 */
package com.reliaquest.employee.model;

import io.micrometer.common.lang.NonNull;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 
 * Employee POJO to be returned as an response
 * 
 */
@Data
@NoArgsConstructor
public class EmployeeDTO {
	/**
	 * Id of the Employee
	 */
	private int id;
	/**
	 * Employee Name
	 */
	@NonNull
	@NotBlank(message = "Name cannot be blank")
	private String name;
	/**
	 * Employee Salary
	 */
	@NonNull
	@Min(value = 1, message = "Salary must be greater than or equal to zero")
	private Integer salary;
	/**
	 * Employee Age
	 */
	@NonNull
	@Min(value = 1, message = "Age must be greater than or equal to zero")
	private Integer age;
	/**
	 * Employe Profile images
	 */
	private String profileImageUrl;
}
