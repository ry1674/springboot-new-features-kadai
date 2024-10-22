package com.example.samuraitravel.form;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;


@Data
@AllArgsConstructor
public class HouseEditForm {
	@NotNull
	private Integer id;
	
	@NotBlank(message = "")
	private String name;
	
	private MultipartFile imageFile;
	
	@NotBlank(message = "")
	private String description;
	
	@NotNull(message = "")
	@Min(value = 1, message = "")
	private Integer price;
	
	@NotNull(message = "")
	@Min(value = 1, message = "")
	private Integer capacity;
	
	@NotBlank(message = "")
	private String postalCode;
	
	@NotBlank(message = "")
	private String address;
	
	@NotBlank(message = "")
	private String phoneNumber;

	
}
