package com.reliaquest.employee.accessor;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.reliaquest.employee.accessor.pojo.EmployeeData;
import com.reliaquest.employee.accessor.pojo.EmployeeReponse;
import com.reliaquest.employee.accessor.pojo.EmployeeRequest;
import com.reliaquest.employee.accessor.pojo.ListEmployeeReponse;
import com.reliaquest.employee.exception.EmployeeAccessorError;
import com.reliaquest.employee.exception.EmployeeAccessorException;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class EmployeeAccessor extends HttpAccessor {
	private static final String ALL_EMPLOYEES_URL = "employees";
	private static final String EMPLOYEE_URL = "employee";
	private static final String EMPLOYEE_CREATE_URL = "create";
	private static final String EMPLOYEE_DELETE_URL = "delete";

	private static final String DELIMETER = "/";

	@Value("${employee.service.baseurl}")
	private String baseUrl;

	public EmployeeAccessor(ObjectMapper objectMapper, RestTemplate restTemplate, RetryTemplate retryTemplate) {
		super();
		this.objectMapper = objectMapper;
		this.restTemplate = restTemplate;
		this.retryTemplate = retryTemplate;
	}

	/**
	 * Fetch all Employee from the API. Considering the fact API doesnt support any
	 * pagination so not implemented that
	 * 
	 * @return List<EmployeeData>
	 */
	public Optional<List<EmployeeData>> fetchAllEmployee() {
		String url = String.join(DELIMETER, baseUrl, ALL_EMPLOYEES_URL);
		log.info("API Call initiated for URL: {}", url);
		return execute(url, ListEmployeeReponse.class, null, HttpMethod.GET).map(ListEmployeeReponse::getData)
				.map(Optional::of).orElse(Optional.empty());
	}

	/**
	 * Fetch Employee having employeeId as param
	 * 
	 * @param id
	 * @return EmployeeData
	 */
	public Optional<EmployeeData> getEmployee(String id) {
		String url = String.join(DELIMETER, baseUrl, EMPLOYEE_URL, id);
		log.info("API Call initiated for URL: {}", url);
		return execute(url, EmployeeReponse.class, null, HttpMethod.GET).map(EmployeeReponse::getData).map(Optional::of)
				.orElse(Optional.empty());
	}

	/**
	 * Idempotency is not taken into consideration in case we need to implement we
	 * need to modify the rety and we relly on 3rd party to provide idempotency as
	 * that is the correct way to do that
	 * 
	 * @param employeeRequest
	 * @return
	 */
	public Optional<EmployeeData> create(EmployeeRequest employeeRequest) {
		String url = String.join(DELIMETER, baseUrl, EMPLOYEE_CREATE_URL);
		log.info("API Call initaed to create Employee for : {}", employeeRequest);
		String request;
		try {
			request = objectMapper.writeValueAsString(employeeRequest);
			return execute(url, EmployeeReponse.class, request, HttpMethod.POST).map(EmployeeReponse::getData)
					.map(Optional::of).orElse(Optional.empty());
		} catch (JsonProcessingException e) {
			log.error("Failed while parsing the request: {}", e.getMessage());
			throw new EmployeeAccessorException(EmployeeAccessorError.JSON_PARSSING_ERROR, e);
		}

	}

	public void delete(String id) {
		String url = String.join(DELIMETER, baseUrl, EMPLOYEE_DELETE_URL, id);
		log.info("API Call to delete initiated for URL: {}", url);
		execute(url, null, null, HttpMethod.DELETE);
	}

}
