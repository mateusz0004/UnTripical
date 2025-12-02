package com.untripical.controller;

import com.untripical.dto.review.guide.ReviewRequestGuideDetailsDTO;
import com.untripical.dto.review.guide.ReviewResponseGuideDetailsDTO;
import com.untripical.dto.review.guide.UpdateRequestGuideDetailsDTO;
import com.untripical.dto.review.place.ReviewRequestPlaceDTO;
import com.untripical.dto.review.place.ReviewResponsePlaceDTO;
import com.untripical.dto.review.place.UpdateRequestPlaceDTO;
import com.untripical.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/review")
@RestController
public class ReviewController {
    @Autowired
    private ReviewService reviewService;

    @GetMapping("/guideDetails/{guideId}")
    public ResponseEntity<List<ReviewResponseGuideDetailsDTO>> getAllGuideDetailsReviews(@PathVariable Long guideId){
        return ResponseEntity.ok(reviewService.getAllGuideDetailsReviews(guideId));
    }

    @PostMapping("/guideDetails")
    public ResponseEntity<ReviewResponseGuideDetailsDTO> addGuideDetailsReview(@RequestBody ReviewRequestGuideDetailsDTO dto){
        return ResponseEntity.ok(reviewService.addGuideDetailsReview(dto));
    }

    @DeleteMapping("/guideDetails/{guideDetailsId}/{orderIndex}")
    public ResponseEntity deleteGuideDetailsReviewWithOrderIndex(@PathVariable Long orderIndex, @PathVariable Long guideDetailsId){
        reviewService.deleteGuideDetailsReviewById(orderIndex, guideDetailsId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/guideDetails/{guideDetailsId}/{orderIndex}")
    public ResponseEntity updateGuideDetailsReviewWithOrderIndex(@RequestBody UpdateRequestGuideDetailsDTO dto, @PathVariable Long orderIndex, @PathVariable Long guideDetailsId){
        return ResponseEntity.ok(reviewService.updateGuideDetailsReview(dto, orderIndex, guideDetailsId));
    }

    @PostMapping("/place")
    public ResponseEntity<ReviewResponsePlaceDTO> addPlaceReview(@RequestBody ReviewRequestPlaceDTO dto){
        return ResponseEntity.ok(reviewService.addPlaceReview(dto));
    }

    @GetMapping("/user/reviews")
    public ResponseEntity<List<ReviewResponsePlaceDTO>> getAllReviewsCreatedByUser (){
        return ResponseEntity.ok(reviewService.getAllReviewsCreatedByUser());
    }

    @GetMapping("/place/{placeId}")
    public ResponseEntity<List<ReviewResponsePlaceDTO>> getAllPlaceReviewsWithId (@PathVariable Long placeId){
        return ResponseEntity.ok(reviewService.getAllPlaceReviews(placeId));
    }

    @DeleteMapping("/place/{placeId}/{orderIndex}")
    public ResponseEntity deletePlaceReviewWithOrderIndex(@PathVariable Long orderIndex, @PathVariable Long placeId){
        reviewService.deletePlaceReviewById(orderIndex, placeId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/place/{placeId}/{orderIndex}")
    public ResponseEntity updatePlaceReviewWithOrderIndex(@RequestBody UpdateRequestPlaceDTO dto, @PathVariable Long orderIndex, @PathVariable Long placeId){
        return ResponseEntity.ok(reviewService.updatePlaceReview(dto, orderIndex, placeId));
    }
}
