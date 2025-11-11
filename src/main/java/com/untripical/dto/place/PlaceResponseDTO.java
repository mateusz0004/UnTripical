package com.untripical.dto.place;

import com.untripical.dto.announcement.AnnouncementResponseDTO;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Value;

import java.util.List;

@Value
public class PlaceResponseDTO {

    Long id;
    String name;
    String city;
    Double latitude;
    Double longitude;
    String photoUrl;
    Long regionId;
    Long userId;
    List<AnnouncementResponseDTO> announcements;
    List<ReviewResponseDTO> reviews;
}
