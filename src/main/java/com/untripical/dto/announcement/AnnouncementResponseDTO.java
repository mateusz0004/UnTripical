package com.untripical.dto.announcement;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.untripical.dto.guideAnnouncementTable.GuideAnnouncementTableResponseDTO;
import com.untripical.enums.AnnouncementType;
import lombok.Value;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Value
public class AnnouncementResponseDTO {
    Long id;
    Date createdAt;
    String nameOfJourney;
    String description;
    AnnouncementType announcementType;
    @JsonFormat(pattern = "dd-MM-yyyy")
    LocalDate date;
    Double price;
    String locationInfo;
    Integer maxParticipants;
    Long placeId;
    List<GuideAnnouncementTableResponseDTO> guideAnnouncementTables;
}
