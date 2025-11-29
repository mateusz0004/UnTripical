package com.untripical.controller;

import com.untripical.dto.review.guide.ReviewRequestGuideDetailsDTO;
import com.untripical.dto.review.guide.ReviewResponseGuideDetailsDTO;
import com.untripical.dto.review.guide.UpdateRequestGuideDetailsDTO;
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
}
