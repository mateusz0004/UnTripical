package com.untripical.dto.guideDetails;

import com.untripical.dto.guideAnnouncementTable.GuideAnnouncementTableResponseDTO;
import com.untripical.dto.review.place.ReviewResponsePlaceDTO;
import com.untripical.enums.ExperienceLevel;
import com.untripical.enums.Specialisation;
import lombok.Value;

import java.util.List;

@Value
public class GuideDetailsResponseDTO {
    Long id;
    String username;
    String phoneNumber;
    String closestBigCity;
    Specialisation specialisation;
    ExperienceLevel experienceLevel;
    int counterOfDidJourney;
    Long regionId;
    List<GuideAnnouncementTableResponseDTO> guideAnnouncements;
    List<ReviewResponsePlaceDTO> reviews;
}