package com.untripical.dto.place;

import com.untripical.dto.announcement.AnnouncementResponseDTO;
import com.untripical.dto.review.place.ReviewResponsePlaceDTO;
import com.untripical.enums.VerificationStatus;
import lombok.Data;

import java.util.List;

@Data
public class PlaceResponseDTO {
    Long id;
    String name;
    String city;
    String addressStreet;
    String addressNumber;
    String placeType;
    String postalCode;
    Boolean isActive;
    String photoUrl;
    VerificationStatus verificationStatus;
    Long regionId;
    Long userId;
    List<AnnouncementResponseDTO> announcements;
    List<ReviewResponsePlaceDTO> reviews;
}
