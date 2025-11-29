package com.untripical.service;

import com.untripical.dto.review.guide.ReviewRequestGuideDetailsDTO;
import com.untripical.dto.review.guide.ReviewResponseGuideDetailsDTO;
import com.untripical.dto.review.guide.UpdateRequestGuideDetailsDTO;
import com.untripical.dto.review.place.ReviewRequestPlaceDTO;
import com.untripical.dto.review.place.ReviewResponsePlaceDTO;
import com.untripical.exception.guideDetails.GuideDetailsDoesNotExist;
import com.untripical.exception.place.PlaceDoesNotExist;
import com.untripical.exception.review.ReviewDoesNotExist;
import com.untripical.mapper.review.ReviewMapper;
import com.untripical.model.GuideDetails;
import com.untripical.model.Place;
import com.untripical.model.Review;
import com.untripical.model.User;
import com.untripical.repository.GuideDetailsRepository;
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
    private GuideDetailsRepository guideDetailsRepository;
    @Autowired
    private UserService userService;


//   public ReviewResponsePlaceDTO getReviewByNumberOfStars (Double starts){
//        Review review = reviewRepository.findByNumberOfStars(starts)
//               .orElseThrow(() -> new ReviewDoesNotExist("Review with this amount of stars does not exist"));
//        return reviewMapper.toResponse(review);
//    }

    public ReviewResponsePlaceDTO addPlaceReview(ReviewRequestPlaceDTO dto){
        User actualUser = userService.getCurrentUser();
        Review entity = reviewMapper.toPlaceEntity(dto);
        entity.setUser(actualUser);
        return reviewMapper.toPlaceResponse(entity);
    }

    public ReviewResponseGuideDetailsDTO addGuideDetailsReview(ReviewRequestGuideDetailsDTO dto){
        User actualUser = userService.getCurrentUser();
        GuideDetails guideDetailsWhereWeAddOpinion = guideDetailsRepository.findById(dto.getGuideId())
                .orElseThrow(()-> new GuideDetailsDoesNotExist("This guide details does not exist"));
        Review entity = reviewMapper.toGuideDetailsEntity(dto);
        entity.setUser(actualUser);
        entity.setGuideDetails(guideDetailsWhereWeAddOpinion);
        int nextIndex = guideDetailsWhereWeAddOpinion.getReviews().size() + 1;
        entity.setOrderIndex(nextIndex);
        guideDetailsWhereWeAddOpinion.getReviews().add(entity);
        reviewRepository.save(entity);
        return reviewMapper.toGuideDetailsResponse(entity);
    }

    public ReviewResponseGuideDetailsDTO updateGuideDetailsReview(UpdateRequestGuideDetailsDTO dto, Long orderIndex, Long guideDetailsId){
        if(!isGuideDetailsReviewActive(orderIndex, guideDetailsId)){
            throw new ReviewDoesNotExist("This review does not exist");
        }
        Long userId = userService.getCurrentUser().getId();
        Review review = reviewRepository.findByOrderIndexAndUser_IdAndGuideDetails_Id(orderIndex, userId, guideDetailsId)
                .orElseThrow(()-> new ReviewDoesNotExist("This review for this user does not exist"));
        if(dto.getDescription()!=null){
            review.setDescription(dto.getDescription());
        }
        if(dto.getNumberOfStars()!=null){
            review.setNumberOfStars(dto.getNumberOfStars());
        }
        reviewRepository.save(review);
        return reviewMapper.toGuideDetailsResponse(review);
    }

    public ReviewResponsePlaceDTO getPlaceReviewById(Long id){
        return reviewRepository.findByPlace_Id(id)
                .map(reviewMapper::toPlaceResponse)
                .orElseThrow(()-> new PlaceDoesNotExist("This place does not exist"));
    }

    public List<ReviewResponsePlaceDTO> getAllPlaceReviews(Long id){
           return reviewRepository.findAllByPlace_Id(id)
                   .stream()
                   .map(reviewMapper::toPlaceResponse)
                   .collect(Collectors.toList());
    }

    public List<ReviewResponseGuideDetailsDTO> getAllGuideDetailsReviews(Long id){
        return reviewRepository.findAllByGuideDetails_Id(id)
                .stream()
                .filter(Review::getIsActive)
                .map(reviewMapper::toGuideDetailsResponse)
                .collect(Collectors.toList());
    }

    public List<ReviewResponseGuideDetailsDTO> addReview(Long id){
        return reviewRepository.findAllByPlace_Id(id)
                .stream()
                .map(reviewMapper::toGuideDetailsResponse)
                .collect(Collectors.toList());
    }

    public ReviewResponseGuideDetailsDTO getGuideDetailsReviewById(Long id){
        return reviewRepository.findByPlace_Id(id)
                .map(reviewMapper::toGuideDetailsResponse)
                .orElseThrow(()-> new ReviewDoesNotExist("This place does not exist"));
    }

    public void deleteGuideDetailsReviewById(Long orderIndex, Long guideDetailsId){
        Long userId = userService.getCurrentUser().getId();
        Review review = reviewRepository.findByOrderIndexAndUser_IdAndGuideDetails_Id(orderIndex, userId, guideDetailsId)
                .orElseThrow(()-> new ReviewDoesNotExist("This review for this user does not exist"));
        if(!review.getIsActive()){
            throw new ReviewDoesNotExist("This review does not exist");
        }
        review.setIsActive(false);
        reviewRepository.save(review);
    }
    public boolean isGuideDetailsReviewActive(Long orderIndex, Long guideDetailsId){
        return reviewRepository.findByOrderIndexAndGuideDetails_Id(orderIndex, guideDetailsId)
                .getIsActive();
    }
}
