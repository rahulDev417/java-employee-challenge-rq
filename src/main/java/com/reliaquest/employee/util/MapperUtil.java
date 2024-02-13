package com.reliaquest.employee.util;

import org.modelmapper.ModelMapper;

import com.reliaquest.employee.accessor.pojo.EmployeeData;
import com.reliaquest.employee.accessor.pojo.EmployeeRequest;
import com.reliaquest.employee.model.EmployeeDTO;

import lombok.experimental.UtilityClass;

@UtilityClass
public class MapperUtil {
	private ModelMapper modelMapper = new ModelMapper();

	public EmployeeDTO convertToDto(EmployeeData post) {
		EmployeeDTO employee = modelMapper.map(post, EmployeeDTO.class);
		return employee;
	}

	public EmployeeRequest convertToRequest(EmployeeDTO post) {
		EmployeeRequest employee = modelMapper.map(post, EmployeeRequest.class);
		return employee;
	}

}
