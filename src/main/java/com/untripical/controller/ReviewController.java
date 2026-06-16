package com.untripical.controller;

import com.untripical.dto.review.guide.ReviewRequestGuideDetailsDTO;
import com.untripical.dto.review.guide.ReviewResponseGuideDetailsDTO;
import com.untripical.dto.review.guide.UpdateRequestGuideDetailsDTO;
import com.untripical.dto.review.place.ReviewRequestPlaceDTO;
import com.untripical.dto.review.place.ReviewResponsePlaceDTO;
import com.untripical.dto.review.place.UpdateRequestPlaceDTO;
import com.untripical.service.ReviewService;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/review")
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
    public ResponseEntity <ReviewResponseGuideDetailsDTO> updateGuideDetailsReviewWithOrderIndex(@RequestBody UpdateRequestGuideDetailsDTO dto, @PathVariable Long orderIndex, @PathVariable Long guideDetailsId){
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

    @GetMapping("/reviews/created-by-user")
    public ResponseEntity <Integer> numbersOfReviewsCreatedByUser(){
        return ResponseEntity.ok(reviewService.numbersOfReviewsCreatedByUser());
    }

    @GetMapping("/reviews/created-for-place/{placeId}")
    public ResponseEntity<Integer> numberOfReviewsForOnePlace (@PathVariable Long placeId){
        return ResponseEntity.ok(reviewService.numberOfReviewsForOnePlace(placeId));
    }

    @GetMapping("/reviews/created-for-guide/{guideDetailsId}")
    public ResponseEntity<Integer> numberOfReviewsCreatedForOneGuideDetails (@PathVariable Long guideDetailsId){
        return ResponseEntity.ok(reviewService.numberOfReviewsCreatedForOneGuideDetails(guideDetailsId));
    }

    @GetMapping("/place/containing/{keyword}")
    public ResponseEntity<List<ReviewResponsePlaceDTO>> getPlaceReviewsContainingKeyword (@PathVariable String keyword){
        return ResponseEntity.ok(reviewService.getPlaceReviewsContainingKeyword(keyword));
    }

    @GetMapping("/guideDetails/containing/{keyword}")
    public ResponseEntity<List<ReviewResponseGuideDetailsDTO>> getGuideDetailsReviewsContainingKeyword (@PathVariable String keyword){
        return ResponseEntity.ok(reviewService.getGuideDetailsReviewsContainingKeyword(keyword));
    }

    @GetMapping("/place/numberOfStars/{numberOfStars}")
    public ResponseEntity<List<ReviewResponsePlaceDTO>> getReviewsForPlaceByNumberOfStars (@PathVariable Double numberOfStars){
        return ResponseEntity.ok(reviewService.getReviewsForPlaceByNumberOfStars(numberOfStars));
    }

    @GetMapping("/guideDetails/numberOfStars/{numberOfStars}")
    public ResponseEntity<List<ReviewResponseGuideDetailsDTO>> getReviewsForGuideDetailsByNumberOfStars (@PathVariable Double numberOfStars){
        return ResponseEntity.ok(reviewService.getReviewsForGuideDetailsByNumberOfStars(numberOfStars));
    }

    //@GetMapping("/place/with/number-of-stars/{numberOfStars}")
    //public ResponseEntity<List<ReviewResponsePlaceDTO>>
}
