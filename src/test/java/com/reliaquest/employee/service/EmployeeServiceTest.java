package com.reliaquest.employee.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.reliaquest.employee.accessor.EmployeeAccessor;
import com.reliaquest.employee.accessor.pojo.EmployeeData;
import com.reliaquest.employee.accessor.pojo.EmployeeRequest;
import com.reliaquest.employee.exception.EmployeeAccessorException;
import com.reliaquest.employee.exception.EmployeeNotFoundException;
import com.reliaquest.employee.model.EmployeeDTO;
import com.reliaquest.employee.util.EmployeeUtility;
import com.reliaquest.employee.util.MapperUtil;

public class EmployeeServiceTest {

	@Mock
	private EmployeeAccessor employeeAccessor;

	private EmployeeService employeeService;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		employeeService = new EmployeeService(employeeAccessor);
	}

	@Test
	void testFetchAllEmployee_Success() {
		// Mock data
		List<EmployeeData> mockEmployeeList = EmployeeUtility.getALlEmployee();
		when(employeeAccessor.fetchAllEmployee()).thenReturn(Optional.of(mockEmployeeList));
		Optional<List<EmployeeDTO>> result = employeeService.fetchAllEmployee();
		assertTrue(result.isPresent());
		assertEquals(24, result.get().size());
	}

	@Test
	void testFetchAllEmployee_EmptyList() {
		when(employeeAccessor.fetchAllEmployee()).thenReturn(Optional.of(Collections.emptyList()));
		Optional<List<EmployeeDTO>> result = employeeService.fetchAllEmployee();
		assertTrue(result.isPresent());
		assertTrue(result.get().isEmpty());
	}

	@Test
	void testFetchAllEmployee_ThrowException() {
		when(employeeAccessor.fetchAllEmployee()).thenThrow(EmployeeAccessorException.class);
		assertThrows(EmployeeAccessorException.class, () -> employeeService.fetchAllEmployee());
	}

	@Test
	void testSearchEmployee_Success() {
		List<EmployeeData> mockEmployeeList = EmployeeUtility.getALlEmployee();
		when(employeeAccessor.fetchAllEmployee()).thenReturn(Optional.of(mockEmployeeList));
		Optional<List<EmployeeDTO>> result = employeeService.searchEmployee("Tiger");
		assertTrue(result.isPresent());
		assertEquals(1, result.get().size());
		assertEquals("Tiger Nixon", result.get().get(0).getName());
	}

	@Test
	void testSearchEmployee_Empty() {
		List<EmployeeData> mockEmployeeList = EmployeeUtility.getALlEmployee();
		when(employeeAccessor.fetchAllEmployee()).thenReturn(Optional.of(mockEmployeeList));
		Optional<List<EmployeeDTO>> result = employeeService.searchEmployee("Rahul");
		assertTrue(result.isEmpty());
	}

	@Test
	void testGetEmployee_Success() {
		EmployeeData mockEmployee = EmployeeUtility.getALlEmployeeById();
		when(employeeAccessor.getEmployee("1")).thenReturn(Optional.of(mockEmployee));
		EmployeeDTO result = employeeService.getEmployee("1");
		assertEquals(mockEmployee.getName(), result.getName());
		assertEquals(mockEmployee.getAge(), result.getAge());
		assertEquals(mockEmployee.getSalary(), result.getSalary());
	}

	@Test
	void testGetEmployee_NotFound() {
		when(employeeAccessor.getEmployee("1")).thenReturn(Optional.empty());
		assertThrows(EmployeeNotFoundException.class, () -> {
			employeeService.getEmployee("1");
		});
	}

	@Test
	void testHighestSalary_Success() {
		List<EmployeeData> mockEmployeeList = EmployeeUtility.getALlEmployee();
		when(employeeAccessor.fetchAllEmployee()).thenReturn(Optional.of(mockEmployeeList));
		Integer result = employeeService.getHighestSalary();
		assertEquals(725000, result);
	}

	@Test
	void testHighestSalary_EmptyList() {
		List<EmployeeData> mockEmployeeList = new ArrayList<>();
		when(employeeAccessor.fetchAllEmployee()).thenReturn(Optional.of(mockEmployeeList));
		Integer result = employeeService.getHighestSalary();
		assertEquals(0, result);
	}

	@Test
	void getTop10HighestPaidEmployee_Success() {
		List<EmployeeData> mockEmployeeList = EmployeeUtility.getALlEmployee();
		when(employeeAccessor.fetchAllEmployee()).thenReturn(Optional.of(mockEmployeeList));
		Optional<List<String>> result = employeeService.getTop10HighestPaidEmployee(10);
		assertTrue(result.isPresent());
		assertEquals(10, result.get().size());
		assertEquals("Paul Byrd", result.get().get(0));
	}

	@Test
	void testCreate_Success() {
		EmployeeDTO mockEmployeeDTO = new EmployeeDTO();
		mockEmployeeDTO.setName("Rahul Anand");
		mockEmployeeDTO.setAge(29);
		mockEmployeeDTO.setSalary(2900);
		EmployeeData employeeData = new EmployeeData();
		employeeData.setName("Rahul Anand");
		employeeData.setAge(29);
		employeeData.setSalary(2900);
		when(employeeAccessor.create(MapperUtil.convertToRequest(mockEmployeeDTO)))
				.thenReturn(Optional.of(employeeData));
		Optional<EmployeeDTO> result = employeeService.create(mockEmployeeDTO);

		assertTrue(result.isPresent());
		assertEquals(mockEmployeeDTO, result.get());
	}

	@Test
	void testCreate_EmptyResponse() {

		EmployeeDTO mockEmployeeDTO = new EmployeeDTO();
		mockEmployeeDTO.setName("Rahul Anand");
		mockEmployeeDTO.setAge(29);
		mockEmployeeDTO.setSalary(2900);
		EmployeeData employeeData = new EmployeeData();
		employeeData.setName("Rahul Anand");
		employeeData.setAge(29);
		employeeData.setSalary(2900);
		when(employeeAccessor.create(MapperUtil.convertToRequest(mockEmployeeDTO))).thenReturn(Optional.empty());

		Optional<EmployeeDTO> result = employeeService.create(mockEmployeeDTO);

		assertTrue(result.isEmpty());
	}

	@Test
	void testCreate_Exception() {
		EmployeeDTO mockEmployeeDTO = new EmployeeDTO();
		mockEmployeeDTO.setName("Rahul Anand");
		mockEmployeeDTO.setAge(29);
		mockEmployeeDTO.setSalary(2900);
		EmployeeData employeeData = new EmployeeData();
		employeeData.setName("Rahul Anand");
		employeeData.setAge(29);
		employeeData.setSalary(2900);

		when(employeeAccessor.create(MapperUtil.convertToRequest(mockEmployeeDTO)))
				.thenThrow(new EmployeeAccessorException());

		assertThrows(EmployeeAccessorException.class, () -> {
			employeeService.create(mockEmployeeDTO);
		});
	}

	@Test
	void testDelete_Success() {

		employeeService.delete("123");
		Mockito.verify(employeeAccessor, Mockito.times(1)).delete("123");
	}

	@Test
	void testDelete_Exception() {

		String employeeId = "123";
		Mockito.doThrow(new RuntimeException("Simulated deletion error")).when(employeeAccessor).delete(employeeId);

		try {
			employeeService.delete(employeeId);
		} catch (Exception e) {
			Mockito.verify(employeeAccessor, Mockito.times(1)).delete(employeeId);
			assertTrue(e instanceof RuntimeException);
			assertEquals("Simulated deletion error", e.getMessage());
		}
	}

}
