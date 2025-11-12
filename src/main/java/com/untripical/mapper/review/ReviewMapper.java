package com.untripical.mapper.review;
import com.untripical.dto.review.ReviewRequestDTO;
import com.untripical.dto.review.ReviewResponseDTO;
import com.untripical.model.Review;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ReviewMapper {
    @Mapping(source = "guideDetailsId", target = "guideDetails.id")
    @Mapping(source = "userId", target = "user.id")
    @Mapping(source = "placeId", target = "place.id")
    Review toEntity(ReviewRequestDTO dto);

    @Mapping(source = "guideDetails.id", target = "guideDetailsId")
    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "place.id", target = "placeId")
    ReviewResponseDTO toResponse(Review entity);
}
