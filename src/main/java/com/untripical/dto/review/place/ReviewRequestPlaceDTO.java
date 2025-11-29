package com.untripical.dto.review.place;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Value;

@Value
public class ReviewRequestPlaceDTO {

    @NotNull(message = "numberOfStars must not be null")
    Double numberOfStars;

    @NotBlank(message = "description must not be null")
    String description;

    @NotNull(message = "placeId must not be null")
    Long placeId;

    @NotNull(message = "userId must not be null")
    Long userId;

    @NotNull(message = "guideDetailsId must not be null")
    Long guideDetailsId;
}

