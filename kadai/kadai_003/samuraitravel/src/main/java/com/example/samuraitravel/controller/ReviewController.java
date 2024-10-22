package com.example.samuraitravel.controller;

import java.net.URI;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.UriComponentsBuilder;

import com.example.samuraitravel.entity.House;
import com.example.samuraitravel.entity.Review;
import com.example.samuraitravel.entity.User;
import com.example.samuraitravel.form.ReviewEditForm;
import com.example.samuraitravel.form.ReviewRegisterForm;
import com.example.samuraitravel.repository.HouseRepository;
import com.example.samuraitravel.repository.ReviewRepository;
import com.example.samuraitravel.security.UserDetailsImpl;
import com.example.samuraitravel.service.ReviewService;


@Controller
@RequestMapping("/review")
public class ReviewController {
	private final ReviewRepository reviewRepository;
	private final HouseRepository houseRepository;
	private final ReviewService reviewService;
	
	public ReviewController(ReviewRepository reviewRepository, HouseRepository houseRepository, ReviewService reviewService) {
		this.reviewRepository = reviewRepository;
		this.houseRepository = houseRepository;
		this.reviewService = reviewService;
	}

	@GetMapping("/{id}")
	public String index(@PathVariable(name = "id") Integer id, Model model, @PageableDefault(page = 0, size = 10, sort = "id", direction = Direction.ASC) Pageable pageable, ReviewRegisterForm reviewRegisterForm) {
    	House house = houseRepository.getReferenceById(id);
    	Page<Review> reviewPage = reviewRepository.findAllByHouseOrderByCreatedAtDesc(house, pageable);
    	
		model.addAttribute("reviewPage", reviewPage);
		model.addAttribute("house" ,house);

		return "review/index";
	}
		
	@GetMapping("/{id}/register")
	public String register(@PathVariable(name = "id") Integer id,  Model model) {
		House house = houseRepository.getReferenceById(id);
        
		model.addAttribute("reviewRegisterForm", new ReviewRegisterForm());		
		model.addAttribute("house", house);
		
		return "review/register";		
	}
	
	@PostMapping("/{id}/create")
	public String create(@PathVariable(name = "id") Integer id, @ModelAttribute @Validated ReviewRegisterForm reviewRegisterForm, BindingResult bindingResult, RedirectAttributes redirectAttributes, @AuthenticationPrincipal UserDetailsImpl userDetailsImpl, Model model) {
        if(bindingResult.hasErrors()) {
            House house = houseRepository.getReferenceById(id);
            model.addAttribute("house", house);
        	return "review/register";
        }
		
		User user = userDetailsImpl.getUser();
        House house = houseRepository.getReferenceById(id);
		reviewService.create(reviewRegisterForm, user, house);
        
		redirectAttributes.addFlashAttribute("successMessage", "レビューを投稿しました。");
		//HTMLで画面作成
		
		return "redirect:/houses/{id}";
	}
	
	@GetMapping("/{id}/edit")
	public String edit(@PathVariable(name = "id") Integer id, Model model) { 
		Review review = reviewRepository.getReferenceById(id);
		House house = review.getHouse();
		ReviewEditForm reviewEditForm = new ReviewEditForm(review.getId(), review.getHouse(), review.getUser(), review.getReview(), review.getDescription());
		
		model.addAttribute("reviewEditForm", reviewEditForm);
		model.addAttribute("house", house);
		
		return "review/edit";
	}
	
	@PostMapping("/{id}/update") // reviewのID
	public String update(@PathVariable(name = "id") Integer id, @ModelAttribute @Validated ReviewEditForm reviewEditForm, BindingResult bindingResult, RedirectAttributes redirectAttributes, UriComponentsBuilder builder) {
	    if (bindingResult.hasErrors()) {
	        return "review/edit";
	    }

	    reviewService.update(reviewEditForm);
	    redirectAttributes.addFlashAttribute("successMessage", "レビューを編集しました。");

	    Review review = reviewRepository.getReferenceById(id);
	    URI location = builder.path("/houses/{houseId}").buildAndExpand(review.getHouse().getId()).toUri();
	    return "redirect:" + location.toString();
	}

	
	@PostMapping("/{id}/delete")
	public String delete(@PathVariable(name = "id") Integer id, RedirectAttributes redirectAttributes, UriComponentsBuilder builder) {
	    Review review = reviewRepository.getReferenceById(id);
	    URI location = builder.path("/houses/{houseId}").buildAndExpand(review.getHouse().getId()).toUri();
	    
		reviewRepository.deleteById(id);
		
		redirectAttributes.addFlashAttribute("successMessage", "レビューを削除しました。");
	    return "redirect:" + location.toString();
	}
	
}


	