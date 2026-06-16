package com.untripical.dto.guideDetails;

import com.untripical.dto.guideAnnouncementTable.GuideAnnouncementTableResponseDTO;
import com.untripical.dto.review.guide.ReviewRequestGuideDetailsDTO;
import com.untripical.dto.review.guide.ReviewResponseGuideDetailsDTO;
import com.untripical.dto.review.place.ReviewResponsePlaceDTO;
import com.untripical.enums.ExperienceLevel;
import com.untripical.enums.Specialisation;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GuideDetailsResponseDTO {
    Long id;
    String username;
    String phoneNumber;
    String closestBigCity;
    Specialisation specialisation;
    ExperienceLevel experienceLevel;
    int counterOfDidJourney;
    Double avgRating;
    Integer numberOfAnnouncements;
    Long regionId;
    List<GuideAnnouncementTableResponseDTO> guideAnnouncements;
    List<ReviewResponseGuideDetailsDTO> reviews;
}