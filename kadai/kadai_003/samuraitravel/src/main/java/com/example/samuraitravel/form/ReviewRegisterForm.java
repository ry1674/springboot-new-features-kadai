package com.example.samuraitravel.form;

import jakarta.validation.constraints.Size;
import lombok.Data;


@Data
public class ReviewRegisterForm {
	private Integer houseId;

	private Integer userId;
	
	private Integer review;

	@Size(min = 1, max = 255)
	private String description;
}

