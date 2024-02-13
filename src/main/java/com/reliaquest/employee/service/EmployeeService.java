package com.reliaquest.employee.service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.reliaquest.employee.accessor.EmployeeAccessor;
import com.reliaquest.employee.accessor.pojo.EmployeeRequest;
import com.reliaquest.employee.exception.EmployeeNotFoundException;
import com.reliaquest.employee.model.EmployeeDTO;
import com.reliaquest.employee.util.MapperUtil;

import io.micrometer.common.lang.NonNull;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class EmployeeService {
	private EmployeeAccessor employeeAccessor;

	public EmployeeService(EmployeeAccessor employeeAccessor) {
		super();
		this.employeeAccessor = employeeAccessor;
	}

	public Optional<List<EmployeeDTO>> fetchAllEmployee() {
		log.info("Fetching all employee data initiated.");
		try {
			return employeeAccessor.fetchAllEmployee().map(employeeDataList -> {
				log.info("Received {} employee data.", employeeDataList.size());

				return employeeDataList.stream().map(MapperUtil::convertToDto).collect(Collectors.toList());
			});
		} catch (Exception e) {
			log.info("Error while fetching all Employee Record", e);
			throw e;
		}
	}

	/**
	 * Filtering is done by name on list of all API as in PI list provided it
	 * doesn't support search API. Ideally this will decrease the efficeny as we are
	 * loading all employee and then searching. It should have an API implementation
	 * to search
	 * 
	 * @param searchString
	 * @return
	 */
	public Optional<List<EmployeeDTO>> searchEmployee(@NonNull final String searchString) {
		log.info("Filtering employee by name: {}", searchString);
		try {
			return fetchAllEmployee()
					.map(employeeList -> employeeList.stream()
							.filter(employee -> employee.getName() != null && employee.getName().contains(searchString))
							.collect(Collectors.toList()))
					.filter(filteredEmployees -> !filteredEmployees.isEmpty()).or(() -> {
						log.info("No employee found with name containing: {}", searchString);
						return Optional.empty();
					});
		} catch (Exception e) {
			log.info("Error while searching Employee with name containg: {}", searchString, e);
			throw e;
		}
	}

	public EmployeeDTO getEmployee(String id) {
		log.info("Fetching employee data for ID: {}", id);
		try {
			return employeeAccessor.getEmployee(id).map(MapperUtil::convertToDto)
					.orElseThrow(EmployeeNotFoundException::new);
		} catch (Exception e) {
			log.info("Error while fetching Employee Record with Id: {}", id, e);
			throw e;
		}
	}

	/**
	 * Maxmum is calulated by loading list of employee. Considering the fact there
	 * are no such API available
	 * 
	 * @return
	 */
	public Integer getHighestSalary() {
		log.info("Fetching Maximum Salary from the list of Employee.");
		try {
			return fetchAllEmployee()
					.map(employees -> employees.stream().mapToInt(EmployeeDTO::getSalary).max().orElse(0)).orElse(0);
		} catch (Exception e) {
			log.info("Error while fetching highest Salary.", e);
			throw e;
		}
	}

	public Optional<List<String>> getTop10HighestPaidEmployee(int limit) {
		log.info("Fetching top {} Employee having upmost salary", limit);
		try {
			return fetchAllEmployee().map(
					employees -> employees.stream().sorted(Comparator.comparingInt(EmployeeDTO::getSalary).reversed())
							.limit(limit).map(EmployeeDTO::getName).collect(Collectors.toList()));
		} catch (Exception e) {
			log.info("Error while fetching top {} employee with highest Salary.", limit, e);
			throw e;
		}
	}

	public Optional<EmployeeDTO> create(@NonNull final EmployeeDTO employee) {
		log.info("Request initaed for createing Employee");
		try {
			final EmployeeRequest request = MapperUtil.convertToRequest(employee);
			return employeeAccessor.create(request).map(MapperUtil::convertToDto).map(Optional::of).orElseGet(() -> {
				log.info("No employee created with request: {}", employee);
				return Optional.empty();
			});
		} catch (Exception exception) {
			log.error("Error while creating Employee", exception);
			throw exception;
		}
	}

	public void delete(String id) {
		log.info("Deleting employee data for ID: {}", id);
		try {
			employeeAccessor.delete(id);
		} catch (Exception e) {
			log.error("Error while deleting Employee", e);
			throw e;
		}
	}
}
