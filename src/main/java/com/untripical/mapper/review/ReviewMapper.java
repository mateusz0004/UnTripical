package com.untripical.mapper.review;
import com.untripical.dto.review.guide.ReviewRequestGuideDetailsDTO;
import com.untripical.dto.review.guide.ReviewResponseGuideDetailsDTO;
import com.untripical.dto.review.place.ReviewRequestPlaceDTO;
import com.untripical.dto.review.place.ReviewResponsePlaceDTO;
import com.untripical.model.Review;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ReviewMapper {
    @Mapping(source = "placeId", target = "place.id")
    Review toPlaceEntity(ReviewRequestPlaceDTO dto);

    @Mapping(source = "guideId", target = "user.id")
    Review toGuideDetailsEntity(ReviewRequestGuideDetailsDTO dto);

    @Mapping(source = "user.username", target = "usernameWhoWroteReview")
    @Mapping(source = "place.id", target = "placeId")
    ReviewResponsePlaceDTO toPlaceResponse(Review entity);

    @Mapping(source = "user.username", target = "usernameWhoWroteReview")
    @Mapping(source = "user.id", target = "guideId")
    ReviewResponseGuideDetailsDTO toGuideDetailsResponse(Review entity);
}
