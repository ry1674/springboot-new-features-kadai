package com.example.samuraitravel.form;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;


@Data
public class ReviewRegisterForm {
	private Integer houseId;

	private Integer userId;
	
	@NotNull(message = "レビューを５段階で評価してください。")
	private Integer review;

	@Size(min = 1, max = 255)
	@NotBlank(message = "コメントを入力してください。")
	private String description;
}

