package com.reliaquest.employee.accessor;

import java.util.Optional;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.reliaquest.employee.exception.EmployeeAccessorError;
import com.reliaquest.employee.exception.EmployeeAccessorException;

import io.micrometer.common.lang.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class HttpAccessor {
	RestTemplate restTemplate;
	RetryTemplate retryTemplate;
	ObjectMapper objectMapper;

	public <T> Optional<T> execute(@NonNull final String url, Class<T> responseType, String body,
			@NonNull final HttpMethod method) {
		try {
			ResponseEntity<String> response = retryTemplate
					.execute((RetryCallback<ResponseEntity<String>, RuntimeException>) context -> {
						log.info("Executing retry attempt # {}, for URL: {}", context.getRetryCount(), url);
						if (method.equals(HttpMethod.GET))
							return restTemplate.getForEntity(url, String.class);
						if (method.equals(HttpMethod.POST))
							return restTemplate.postForEntity(url, body, String.class);
						if (method.equals(HttpMethod.DELETE))
							return restTemplate.exchange(url, HttpMethod.DELETE, new HttpEntity<>(body), String.class);
						else
							throw new IllegalArgumentException("Unsupported HTTP method: " + method);
					});
			;
			if (response.getStatusCode() == HttpStatus.OK) {
				if(responseType != null)
					return Optional.of(objectMapper.readValue(response.getBody(), responseType));
				return Optional.empty();
			} else {
				log.error("Error while getting data from Employee Service with status code: {}",
						response.getStatusCode());
				throw new EmployeeAccessorException(EmployeeAccessorError.SERVICE_ERROR);
			}
		} catch (HttpClientErrorException.NotFound e) {
			log.error("API Reource not found. {}", e.getMessage());
			throw new EmployeeAccessorException(EmployeeAccessorError.API_RESOURCE_NOT_FOUND, e);
		} catch (JsonProcessingException e) {
			log.error("Failed while parsing to employeeData: {}", e.getMessage());
			throw new EmployeeAccessorException(EmployeeAccessorError.JSON_PARSSING_ERROR, e);
		} catch (Exception e) {
			log.error("Error while fetching employee data.", e);
			throw new EmployeeAccessorException(EmployeeAccessorError.SERVICE_NETWORK_ERROR, e);
		}
	}

}
