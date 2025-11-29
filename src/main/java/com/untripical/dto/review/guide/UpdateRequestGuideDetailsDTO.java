package com.untripical.dto.review.guide;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Value;

@Value
public class UpdateRequestGuideDetailsDTO {
    Double numberOfStars;
    String description;
}
