package com.untripical.dto.review.guide;

import com.untripical.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReviewResponseGuideDetailsDTO {
    Double numberOfStars;
    String description;
    Date createdAt;
    Long guideId;
    String usernameWhoWroteReview;
    Long orderIndex;
}
