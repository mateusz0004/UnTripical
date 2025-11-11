package com.untripical.dto.guideAnnouncementTable;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Date;

@Data
public class GuideAnnouncementTableRequestDTO {
    @NotBlank(message = "Name must not be blank")
    private String name;

    @NotNull(message = "Date must not be null")
    private Date date;

    @NotNull(message = "Total distance must not be null")
    @Min(value = 0, message = "Total distance must be non-negative")
    private Double totalDistance;

    @NotNull(message = "Guide details ID must not be null")
    private Long guideDetailsId;

    @NotNull(message = "announcement ID")
    private Long announcementId;
}