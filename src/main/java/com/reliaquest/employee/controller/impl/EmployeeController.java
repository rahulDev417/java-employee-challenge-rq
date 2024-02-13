package com.reliaquest.employee.controller.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.reliaquest.employee.model.EmployeeDTO;
import com.reliaquest.employee.service.EmployeeService;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class EmployeeController {

	private EmployeeService employeeService;

	public EmployeeController(EmployeeService employeeService) {
		super();
		this.employeeService = employeeService;
	}

	/**
	 * In Case employee list is found will return employee list with HTTP status as
	 * 200 ok. while in case no employee data is found it will be 204 No content as
	 * 404 is not correctly suited for these scenarios as we are not searching for
	 * specifically employee
	 * 
	 * @return List<EmployeeDTO>
	 */
	@GetMapping
	public ResponseEntity<List<EmployeeDTO>> getAllEmployees() {
		final Optional<List<EmployeeDTO>> employeeList = employeeService.fetchAllEmployee();
		if (employeeList.isPresent()) {
			return ResponseEntity.ok(employeeList.get());
		}
		return ResponseEntity.noContent().build();
	}

	/**
	 * Fetch employee list whose name contains or matches the search String.
	 * 
	 * Returning 204 in case no employee is found as this is not correct use case to
	 * throw 404 as search is not made for speific employee
	 * 
	 * @param searchString
	 * @return List<EmployeeDTO>
	 */
	@GetMapping("/search/{searchString}")
	public ResponseEntity<List<EmployeeDTO>> getEmployeesByNameSearch(
			@PathVariable("searchString") String searchString) {
		log.info("Employee search with request: {} iniated", searchString);
		final Optional<List<EmployeeDTO>> employeeList = employeeService.searchEmployee(searchString);
		if (employeeList.isPresent()) {
			log.info("Employee found with :", searchString);
			return ResponseEntity.ok(employeeList.get());
		}
		return ResponseEntity.noContent().build();
	}

	/**
	 * 404 is thrown in case employee is not found that is handled by Conroller
	 * Advice. Look {@RestExceptionHandler}
	 * 
	 * @param id
	 * @return
	 */
	@GetMapping("/{id}")
	public ResponseEntity<EmployeeDTO> getEmployeeById(@PathVariable("id") String id) {
		log.info("Employee search with id: {} iniated", id);
		final EmployeeDTO employee = employeeService.getEmployee(id);

		log.info("Employee found with :", id);
		return ResponseEntity.ok(employee);
		// Thrown 404 in case no employee is found handled via RestExceptionHandler

	}

	@GetMapping("/highestSalary")
	public ResponseEntity<Integer> getHighestSalaryOfEmployees() {
		log.info("Request to fetch Employee with highest salary initated");
		return ResponseEntity.ok(employeeService.getHighestSalary());
	}

	@GetMapping("/topTenHighestEarningEmployeeNames")
	public ResponseEntity<List<String>> getTopTenHighestEarningEmployeeNames() {
		log.info("Request to fetch top 10 Employee with highest salary initated");
		final Optional<List<String>> employees = employeeService.getTop10HighestPaidEmployee(10);
		if (employees.isPresent()) {
			log.info("Fetched 10 employee with highest salary");
			return ResponseEntity.ok(employees.get());
		}
		log.info("No data found");
		return ResponseEntity.noContent().build();
	}

	/**
	 * Idempotency is not taken into consideration as this should be managed by
	 * Employee Service the one whose API is called here
	 * 
	 * @param employeeInput
	 * @return
	 */
	@PostMapping(consumes = "application/json")
	public ResponseEntity<EmployeeDTO> createEmployee(@Valid @RequestBody EmployeeDTO employeeInput) {
		log.info("Request for creating employee has been initaed");
		final Optional<EmployeeDTO> emplyee = employeeService.create(employeeInput);
		if (emplyee.isPresent()) {
			return ResponseEntity.ok(emplyee.get());
		}
		return ResponseEntity.badRequest().body(null);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<String> deleteEmployeeById(@PathVariable("id") String id) {
		log.info("Request to delete Employee : {} iniated.", id);
		employeeService.delete(id);
		return ResponseEntity.ok("SUCCESS");
	}

}
