package com.example.samuraitravel.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.samuraitravel.entity.House;
import com.example.samuraitravel.entity.Review;
import com.example.samuraitravel.entity.User;
import com.example.samuraitravel.form.ReviewEditForm;
import com.example.samuraitravel.form.ReviewRegisterForm;
import com.example.samuraitravel.repository.ReviewRepository;

@Service
public class ReviewService {
	private final ReviewRepository reviewRepository;
	
	public ReviewService(ReviewRepository reviewRepository) {
		this.reviewRepository = reviewRepository;
	}
	
	@Transactional
	public void create(ReviewRegisterForm reviewRegisterForm, User user, House house) {
		Review review = new Review();
				
		review.setUser(user); 
		review.setHouse(house);
		review.setReview(reviewRegisterForm.getReview());
		review.setDescription(reviewRegisterForm.getDescription());

		reviewRepository.save(review);
	}
	
	@Transactional
	public void update(ReviewEditForm reviewEditForm) {
		Review review = reviewRepository.getReferenceById(reviewEditForm.getId());
		
		review.setReview(reviewEditForm.getReview());
		review.setDescription(reviewEditForm.getDescription());
		
		reviewRepository.save(review);
	}

}
