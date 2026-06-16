package com.untripical.dto.announcement;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.untripical.dto.guideAnnouncementTable.GuideAnnouncementTableRequestDTO;
import com.untripical.enums.AnnouncementType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Value;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Value
public class AnnouncementRequestDTO {
    @NotBlank(message = "nameOfJourney must not be blank")
    String nameOfJourney;

    @NotBlank(message = "description must not be blank")
    String description;

    @NotNull(message = "announcementType must not be null")
    AnnouncementType announcementType;

    @NotNull(message = "date must not be null")
    @JsonFormat(pattern = "dd-MM-yyyy")
    LocalDate date;

    @NotNull(message = "price must not be null")
    @Min(0)
    Double price;

    @NotBlank(message = "locationInfo must not be blank")
    String locationInfo;

    @NotNull(message = "maxParticipants must not be null")
    @Positive
    Integer maxParticipants;

    @NotNull(message = "placeId must not be null")
    Long placeId;
}
