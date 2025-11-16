package com.untripical.service;

import com.untripical.dto.review.ReviewResponseDTO;
import com.untripical.exception.review.ReviewDoesNotExist;
import com.untripical.mapper.review.ReviewMapper;
import com.untripical.model.GuideDetails;
import com.untripical.model.Review;
import com.untripical.repository.PlaceRepository;
import com.untripical.repository.ReviewRepository;
import com.untripical.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional

public class ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;
    @Autowired
    private ReviewMapper reviewMapper;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PlaceRepository placeRepository;
    @Autowired
    private GuideDetails guideDetails;


    public ReviewResponseDTO getReviewByNumberOfStars (Double starts){
        Review review = reviewRepository.findByNumberOfStars(starts)
                .orElseThrow(() -> new ReviewDoesNotExist("Review with this amount of stars does not exist"));
        return reviewMapper.toResponse(review);
    }

    public List<ReviewResponseDTO> getAllReviews(){
        return reviewRepository.findAll()
                .stream()
                .map(reviewMapper::toResponse)
                .collect(Collectors.toList());
    }

    //public ReviewResponseDTO addReview() // dokończyć




}
