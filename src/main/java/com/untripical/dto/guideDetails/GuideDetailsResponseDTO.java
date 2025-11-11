package com.untripical.dto.guideDetails;

import com.untripical.untripical.dto.guideAnnouncementTable.GuideAnnouncementTableResponseDTO;
import com.untripical.untripical.dto.review.ReviewResponseDTO;
import com.untripical.enums.ExperienceLevel;
import com.untripical.enums.Specialisation;
import lombok.Value;

import java.util.List;

@Value
public class GuideDetailsResponseDTO {
    private final Long id;
    private final String guideName;
    private final String phoneNumber;
    private final String closestBigCity;
    private final Specialisation specialisation;
    private final ExperienceLevel experienceLevel;
    private final int counterOfDidJourney;
    private final Long userId;
    private final Long regionId;
    private final List<GuideAnnouncementTableResponseDTO> guideAnnouncements;
    private final List<ReviewResponseDTO> reviews;
}