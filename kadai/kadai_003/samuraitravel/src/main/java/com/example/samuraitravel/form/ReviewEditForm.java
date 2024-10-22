package com.example.samuraitravel.form;

import com.example.samuraitravel.entity.House;
import com.example.samuraitravel.entity.User;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;


@Data
@AllArgsConstructor
public class ReviewEditForm {

	private Integer id;
		
	private House house;

	private User user;
	
	private Integer review;

	@Size(min = 1, max = 255)
	private String description;
}
