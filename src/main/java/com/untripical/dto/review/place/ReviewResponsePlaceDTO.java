package com.untripical.dto.review;

import com.untripical.model.GuideDetails;
import com.untripical.model.Place;
import com.untripical.model.User;
import lombok.Value;

import java.util.Date;

@Value
public class ReviewResponseDTO {

    Long id;
    Double numberOfStars;
    String description;
    Date createdAt;
    Long placeId;
    Long userId;
    Long guideDetailsId;
}
