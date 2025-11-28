package com.untripical.dto.guideAnnouncementTable;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Date;

@Data
public class GuideAnnouncementTableRequestDTO {

    @NotNull(message = "Guide details ID must not be null")
    Long guideDetailsId;

    @NotNull(message = "announcement ID")
    Long announcementId;
}