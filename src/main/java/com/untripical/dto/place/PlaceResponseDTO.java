package com.untripical.dto.place;

import com.untripical.dto.announcement.AnnouncementResponseDTO;
import com.untripical.dto.review.place.ReviewResponsePlaceDTO;
import com.untripical.enums.VerificationStatus;
import lombok.Value;

import java.util.List;

@Value
public class PlaceResponseDTO {
    Long id;
    String name;
    String city;
    VerificationStatus verificationStatus;
    String photoUrl;
    Long regionId;
    Long userId;
    List<AnnouncementResponseDTO> announcements;
    List<ReviewResponsePlaceDTO> reviews;
}
