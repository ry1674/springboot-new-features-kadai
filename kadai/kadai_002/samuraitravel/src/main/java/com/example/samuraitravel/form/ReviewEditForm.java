package com.example.samuraitravel.form;

import com.example.samuraitravel.entity.House;
import com.example.samuraitravel.entity.User;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;


@Data
@AllArgsConstructor
public class ReviewEditForm {

	private Integer id;
		
	private House house;

	private User user;
	
	@NotNull(message = "レビューを５段階で評価してください。")
	private Integer review;

	@Size(min = 1, max = 255)
	@NotBlank(message = "コメントを入力してください。")
	@NotNull(message = "コメントを入力してください。")
	private String description;
}
