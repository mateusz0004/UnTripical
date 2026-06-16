package com.untripical.repository;

import com.untripical.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    Optional<Review> findByNumberOfStars(Double numberOfStars);
    List<Review> findAllByGuideDetails_Id(Long id);
    List<Review> findAllByUser_Id(Long id);
    List<Review> findAllByPlace_Id(Long id);
    Review findByOrderIndexAndGuideDetails_Id(Long orderIndex,Long guideDetailsId);
    Review findByOrderIndexAndPlace_Id(Long orderIndex,Long guideDetailsId);
    Optional<Review> findByPlace_Id(Long id);
    Optional<Review> findByOrderIndexAndUser_IdAndGuideDetails_Id(Long orderIndex, Long userId, Long guideDetailsId);
    Optional<Review> findByOrderIndexAndUser_IdAndPlace_Id(Long orderIndex, Long userId, Long placeId);
    int countByUser_Id(Long userId);
    int countByPlace_Id(Long placeId);
    int countByGuideDetails_Id(Long userId);
    List<Review> findByPlaceIsNotNullAndDescriptionContaining(String keyword);
    List<Review> findByGuideDetailsIsNotNullAndDescriptionContaining(String keyword);
    List<Review> findByPlaceIsNotNullAndNumberOfStars(Double numberOfStars);
    List<Review> findByGuideDetailsIsNotNullAndNumberOfStars(Double numberOfStars);
}
