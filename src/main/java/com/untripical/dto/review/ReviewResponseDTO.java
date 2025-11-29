package com.untripical.dto.review.place;

import com.untripical.model.User;
import lombok.Value;

import java.util.Date;

@Value
public class ReviewResponsePlaceDTO {
    Double numberOfStars;
    String description;
    Date createdAt;
    Long placeId;
    String usernameWhoWroteReview;
    Long orderIndex;
}
