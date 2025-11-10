package com.untripical.untripical.dto.announcement;

import com.untripical.untripical.dto.guideAnnouncementTable.GuideAnnouncementTableRequestDTO;
import com.untripical.untripical.enums.AnnouncementType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Value;

import java.util.Date;
import java.util.List;

@Value
public class AnnouncementRequestDTO {
    @NotBlank(message = "nameOfJourney must not be blank")
    private String nameOfJourney;

    @NotBlank(message = "description must not be blank")
    private String description;

    @NotNull(message = "announcementType must not be null")
    private AnnouncementType announcementType;

    @NotNull(message = "date must not be null")
    private Date date;

    @NotNull(message = "price must not be null")
    @Min(0)
    private Double price;

    @NotBlank(message = "locationInfo must not be blank")
    private String locationInfo;

    @NotNull(message = "maxParticipants must not be null")
    @Positive
    private Integer maxParticipants;

    @NotNull(message = "isActive must not be null")
    private Boolean isActive;

    private List<GuideAnnouncementTableRequestDTO> guideAnnouncementTableRequestDTOList;

    @NotNull(message = "placeId must not be null")
    private Long placeId;
}
