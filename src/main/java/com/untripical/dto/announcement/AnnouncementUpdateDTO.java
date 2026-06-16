package com.untripical.dto.announcement;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.untripical.enums.AnnouncementType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import lombok.Value;

import java.time.LocalDate;


@Value
public class AnnouncementUpdateDTO {
    String nameOfJourney;
    String description;
    AnnouncementType announcementType;
    @JsonFormat(pattern = "dd-MM-yyyy")
    LocalDate date;
    @Min(0)
    Double price;
    String locationInfo;
    @Positive(message = "maxParticipants must be non-negative")
    Integer maxParticipants;
    Boolean isActive;
    Long placeId;
}
