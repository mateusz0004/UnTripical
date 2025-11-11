package com.untripical.dto.announcement;

import com.untripical.enums.AnnouncementType;
import lombok.Value;

import java.util.Date;
@Value
public class AnnouncementResponseDTO {
    private final Long id;
    private final Date createdAt;
    private final String nameOfJourney;
    private final String description;
    private final AnnouncementType announcementType;
    private final Date date;
    private final Double price;
    private final String locationInfo;
    private final Integer maxParticipants;
    private final Boolean isActive;
    private final Long placeId;
    private final List<GuideAnnouncementTableResponseDTO> guideAnnouncementTables;
}
