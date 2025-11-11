package com.untripical.dto.guideAnnouncementTable;

import lombok.Value;

import java.util.Date;

@Value
public class GuideAnnoncementTableResponseDTO {
    private final Long id;
    private final String name;
    private final Date date;
    private final Double totalDistance;
    private final Long announcementId;
    private final Long guideDetailsId;
}

