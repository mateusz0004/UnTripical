package com.untripical.dto.guideDetails;

import com.untripical.dto.guideAnnouncementTable.GuideAnnouncementTableResponseDTO;
import com.untripical.dto.review.ReviewResponseDTO;
import com.untripical.enums.ExperienceLevel;
import com.untripical.enums.Specialisation;
import com.untripical.enums.UserRole;
import com.untripical.model.GuideAnnouncementTable;
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
    List<ReviewResponseDTO> reviews;
}