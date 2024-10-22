package com.example.samuraitravel.controller;

import java.net.URI;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.UriComponentsBuilder;

import com.example.samuraitravel.entity.Favorite;
import com.example.samuraitravel.entity.House;
import com.example.samuraitravel.entity.Review;
import com.example.samuraitravel.entity.User;
import com.example.samuraitravel.form.ReservationInputForm;
import com.example.samuraitravel.repository.FavoriteRepository;
import com.example.samuraitravel.repository.HouseRepository;
import com.example.samuraitravel.repository.ReviewRepository;
import com.example.samuraitravel.security.UserDetailsImpl;
import com.example.samuraitravel.service.FavoriteService;
 
@Controller
@RequestMapping("/houses")
public class HouseController {
	private final HouseRepository houseRepository;  //インスタンス化    newを使わなくていいのはコンストラクタインジェクションのため。
    private final FavoriteRepository favoriteRepository;
    private final ReviewRepository reviewRepository;
	private final FavoriteService favoriteService;
    
    public HouseController(HouseRepository houseRepository, FavoriteRepository favoriteRepository, ReviewRepository reviewRepository, FavoriteService favoriteService) {
        this.houseRepository = houseRepository;   
        this.favoriteRepository = favoriteRepository;
        this.reviewRepository = reviewRepository;
        this.favoriteService = favoriteService;
    }     
   
    @GetMapping
    public String index(@RequestParam(name = "keyword", required = false) String keyword,
                        @RequestParam(name = "area", required = false) String area,
                        @RequestParam(name = "price", required = false) Integer price,    
                        @RequestParam(name = "order", required = false) String order,
                        @PageableDefault(page = 0, size = 10, sort = "id", direction = Direction.ASC) Pageable pageable,
                        Model model) 
    {
    	
        Page<House> housePage;
        
        if (keyword != null && !keyword.isEmpty()) {
             if (order != null && order.equals("priceAsc")) {
                 housePage = houseRepository.findByNameLikeOrAddressLikeOrderByPriceAsc("%" + keyword + "%", "%" + keyword + "%", pageable);
             } else {
                 housePage = houseRepository.findByNameLikeOrAddressLikeOrderByCreatedAtDesc("%" + keyword + "%", "%" + keyword + "%", pageable);
             }            
        } else if (area != null && !area.isEmpty()) {
             if (order != null && order.equals("priceAsc")) {
                 housePage = houseRepository.findByAddressLikeOrderByPriceAsc("%" + area + "%", pageable);
             } else {
                 housePage = houseRepository.findByAddressLikeOrderByCreatedAtDesc("%" + area + "%", pageable);
             }            
        } else if (price != null) {
             if (order != null && order.equals("priceAsc")) {
                 housePage = houseRepository.findByPriceLessThanEqualOrderByPriceAsc(price, pageable);
             } else {
                 housePage = houseRepository.findByPriceLessThanEqualOrderByCreatedAtDesc(price, pageable);
             }            
        } else {
             if (order != null && order.equals("priceAsc")) {
                 housePage = houseRepository.findAllByOrderByPriceAsc(pageable);
             } else {
                 housePage = houseRepository.findAllByOrderByCreatedAtDesc(pageable);   
             }            
        }                

         
        model.addAttribute("housePage", housePage);
        model.addAttribute("keyword", keyword);
        model.addAttribute("area", area);
        model.addAttribute("price", price);                              
        model.addAttribute("order", order); 
        
        return "houses/index";
    }
        
    @GetMapping("/{id}")
	public String show(@PathVariable(name = "id") Integer id, @AuthenticationPrincipal UserDetailsImpl userDetailsImpl, Model model) {
    	House house = houseRepository.getReferenceById(id);
		List<Review> reviewList = reviewRepository.findTop6ByHouseOrderByCreatedAtDesc(house);

	//　　　レビュー済か判断するためにDBにアクセスする 	
	    List<Review> postList;
	   	if(userDetailsImpl != null) {
	   	User user = userDetailsImpl.getUser();
	    postList = reviewRepository.findByUser(user);
	    model.addAttribute("postList", postList);
	    }
	   	
//		お気に入り済かどうか判断するためにDBにアクセス
		Favorite favorite;
		if(userDetailsImpl != null) {
			User user = userDetailsImpl.getUser();
			favorite = favoriteRepository.findByUserAndHouse(user, house);
			model.addAttribute("favorite", favorite);
		}
    	
    	model.addAttribute("house", house);
        model.addAttribute("reservationInputForm", new ReservationInputForm());
		model.addAttribute("reviewList", reviewList);
        
    	return "houses/show";
    }
    
    @GetMapping("/{id}/create")
    public String create(@PathVariable(name = "id") Integer id, @AuthenticationPrincipal UserDetailsImpl userDetailsImpl, RedirectAttributes redirectAttributes) {
    	if(userDetailsImpl != null) {
    		House house = houseRepository.getReferenceById(id);
    		User user = userDetailsImpl.getUser();
    		Favorite favorite = favoriteRepository.findByUserAndHouse(user, house);
    		if(favorite != null) {
    			favoriteService.update(house, user);
    		} else if(favorite == null) {
    			favoriteService.create(house, user);
    		}
    	}
    	
    	return "redirect:/houses/{id}";
    }
    
//	お気に入り解除
	@GetMapping("/{id}/delete") //お気に入りID
	public String delete(@PathVariable(name = "id") Integer id, RedirectAttributes redirectAttributes, UriComponentsBuilder builder) {
		Favorite favorite =  favoriteRepository.getReferenceById(id);
		URI location = builder.path("/houses/{houseId}").buildAndExpand(favorite.getHouse().getId()).toUri();
		
		favoriteService.booleanDelete(favorite.getUser(), favorite.getHouse());
		
		return "redirect:" + location.toString();
	}
}

