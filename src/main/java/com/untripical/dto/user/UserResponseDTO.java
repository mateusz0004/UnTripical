package com.untripical.dto.user;

import com.untripical.dto.place.PlaceResponseDTO;
import com.untripical.dto.review.ReviewResponseDTO;
import com.untripical.dto.tripPlan.TripPlanResponseDTO;
import com.untripical.enums.UserRole;
import lombok.Value;

import java.util.Date;
import java.util.List;

@Value
public class UserResponseDTO {
    Long id;
    String emails;
    String username;
    String password;
    Boolean isActive;
    Date createdAt;
    UserRole userRole;
    List<PlaceResponseDTO> places;
    List<ReviewResponseDTO> review;
    List<TripPlanResponseDTO> tripPlans;
}
