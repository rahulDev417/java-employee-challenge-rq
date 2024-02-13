package com.reliaquest.employee.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.RetryPolicy;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.retry.backoff.ExponentialBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
@EnableRetry
public class JavaEmployeeChallengeRqConfig {
	@Value("${employee.service.api.retry}")
	private Integer retry;
	@Value("${employee.service.api.initialInterval}")
	private Integer initailInterval;
	@Value("${employee.service.api.multiplier}")
	private Double multiplier;

    /**
     * Configure Retry strategy and backoff strategy
     * 
     * @return RetryTemplate
     */
    @Bean
    RetryTemplate getRetryTemplate() {
		final RetryTemplate retryTemplate = new RetryTemplate();
		final RetryPolicy retryPolicy = new SimpleRetryPolicy(retry);
		retryTemplate.setRetryPolicy(retryPolicy);

		// Configure backoff policy
		ExponentialBackOffPolicy backOffPolicy = new ExponentialBackOffPolicy();
		backOffPolicy.setInitialInterval(initailInterval);
		backOffPolicy.setMultiplier(multiplier);
		retryTemplate.setBackOffPolicy(backOffPolicy);
		return retryTemplate;
	}
	
	@Bean
	RestTemplate getRestTemplate() {
		return new RestTemplate();
	}
	
	@Bean
	ObjectMapper getObjectMapper() {
		return new ObjectMapper();
	}
}
