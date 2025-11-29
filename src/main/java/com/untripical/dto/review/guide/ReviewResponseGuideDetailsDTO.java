package com.untripical.dto.review.guide;

import com.untripical.model.User;
import lombok.Value;

import java.util.Date;

@Value
public class ReviewResponseGuideDetailsDTO {
    Double numberOfStars;
    String description;
    Date createdAt;
    Long guideId;
    String usernameWhoWroteReview;
    Long orderIndex;
}
