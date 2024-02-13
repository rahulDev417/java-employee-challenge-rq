package com.reliaquest.employee.accessor;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.RetryPolicy;
import org.springframework.retry.backoff.ExponentialBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.reliaquest.employee.accessor.pojo.EmployeeData;
import com.reliaquest.employee.accessor.pojo.EmployeeReponse;
import com.reliaquest.employee.accessor.pojo.EmployeeRequest;
import com.reliaquest.employee.exception.EmployeeAccessorException;
import com.reliaquest.employee.util.EmployeeUtility;

public class EmployeeAccessorTest {
	private RestTemplate restTemplate;
	private RetryTemplate retryTemplate;
	private ObjectMapper objectMapper;
	private EmployeeAccessor employeeAccessor;

	@BeforeEach
	void setUp() {
		restTemplate = mock(RestTemplate.class);
		retryTemplate = new RetryTemplate();
		final RetryPolicy retryPolicy = new SimpleRetryPolicy(3);
		retryTemplate.setRetryPolicy(retryPolicy);

		// Configure backoff policy
		ExponentialBackOffPolicy backOffPolicy = new ExponentialBackOffPolicy();
		backOffPolicy.setInitialInterval(1000);
		backOffPolicy.setMultiplier(2);
		retryTemplate.setBackOffPolicy(backOffPolicy);
		objectMapper = new ObjectMapper();
		employeeAccessor = new EmployeeAccessor(objectMapper, restTemplate, retryTemplate);
	}

	@Test
	void testFetchAllEmployee_Success() throws Throwable {
		String responseBody = EmployeeUtility.fetchAllEmployee();
		ResponseEntity<String> responseEntity = new ResponseEntity<>(responseBody, HttpStatus.OK);

		when(restTemplate.getForEntity(Mockito.eq("null/employees"), Mockito.eq(String.class)))
				.thenReturn(responseEntity);
		Optional<List<EmployeeData>> result = employeeAccessor.fetchAllEmployee();

		assertTrue(result.isPresent());
		assertEquals(24, result.get().size());
	}

	@Test
	void testFetchAllEmployee_NotFound() throws Throwable {
		when(restTemplate.getForEntity(Mockito.eq("null/employees"), Mockito.eq(String.class)))
				.thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND));

		assertThrows(EmployeeAccessorException.class, () -> employeeAccessor.fetchAllEmployee());
	}

	@Test
	void testGetEmployee_Success() throws Throwable {
		String responseBody = EmployeeUtility.fetch1Employee();
		ResponseEntity<String> responseEntity = new ResponseEntity<>(responseBody, HttpStatus.OK);

		when(restTemplate.getForEntity(Mockito.eq("null/employee/1"), Mockito.eq(String.class)))
				.thenReturn(responseEntity);

		Optional<EmployeeData> result = employeeAccessor.getEmployee("1");

		assertTrue(result.isPresent());
		assertEquals(1, result.get().getId());
		assertEquals("Rahul Anand", result.get().getName());
	}

	@Test
	void testGetEmployee_NotFound() throws Throwable {
		String responseBody = EmployeeUtility.fetchEmptyEmployee();
		ResponseEntity<String> responseEntity = new ResponseEntity<>(responseBody, HttpStatus.OK);

		when(restTemplate.getForEntity(Mockito.eq("null/employee/1"), Mockito.eq(String.class)))
				.thenReturn(responseEntity);
		Optional<EmployeeData> result = employeeAccessor.getEmployee("1");
		assertFalse(result.isPresent());
	}

	@Test
	void testCreate_Success() throws Throwable {
		EmployeeRequest request = new EmployeeRequest();
		request.setName("Rahul Anand");
		request.setAge(29);
		request.setSalary(2900);

		String requestBody = objectMapper.writeValueAsString(request);
		String responseBody = EmployeeUtility.fetch1Employee();
		ResponseEntity<String> responseEntity = new ResponseEntity<>(responseBody, HttpStatus.OK);

		when(restTemplate.postForEntity(Mockito.eq("null/create"), Mockito.eq(requestBody), Mockito.eq(String.class)))
				.thenReturn(responseEntity);

		Optional<EmployeeData> result = employeeAccessor.create(request);

		assertTrue(result.isPresent());
		assertEquals(1, result.get().getId());
		assertEquals("Rahul Anand", result.get().getName());
	}

	@Test
	void testCreate_ParsingError() throws Exception {
		EmployeeRequest request = new EmployeeRequest();
		request.setName("Rahul Anand");
		request.setAge(29);
		request.setSalary(2900);
		ObjectMapper objectMapper1 = mock(ObjectMapper.class);
		when(objectMapper1.writeValueAsString(request)).thenThrow(JsonProcessingException.class);

		assertThrows(EmployeeAccessorException.class, () -> employeeAccessor.create(request));
	}

	@Test
	void testDelete_Success() throws Throwable {
		ResponseEntity<String> responseEntity = new ResponseEntity<>(HttpStatus.OK);

		when(restTemplate.exchange(Mockito.eq("null/delete/1"), Mockito.eq(HttpMethod.DELETE),
				Mockito.eq(HttpEntity.EMPTY), Mockito.eq(String.class))).thenReturn(responseEntity);

		assertDoesNotThrow(() -> employeeAccessor.delete("1"));
	}

	@Test
	void testRetry() throws Throwable {
		when(restTemplate.exchange(Mockito.eq("null/delete/1"), Mockito.eq(HttpMethod.DELETE),
				Mockito.eq(HttpEntity.EMPTY), Mockito.eq(String.class)))
				.thenThrow(new HttpClientErrorException(HttpStatus.INTERNAL_SERVER_ERROR));

		assertThrows(EmployeeAccessorException.class, () -> employeeAccessor.delete("1"));
		Mockito.verify(restTemplate, Mockito.times(3)).exchange(Mockito.eq("null/delete/1"),
				Mockito.eq(HttpMethod.DELETE), Mockito.eq(HttpEntity.EMPTY), Mockito.eq(String.class));
	}

}
