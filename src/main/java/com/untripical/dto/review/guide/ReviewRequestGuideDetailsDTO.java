package com.untripical.dto.review.guide;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Value;

@Value
public class ReviewRequestGuideDetailsDTO {
    @NotNull(message = "numberOfStars must not be null")
    Double numberOfStars;

    @NotBlank(message = "description must not be null")
    String description;

    @NotNull(message = "userId must not be null")
    Long guideId;
}


