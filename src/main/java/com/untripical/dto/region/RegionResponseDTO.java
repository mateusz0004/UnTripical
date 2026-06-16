package com.untripical.dto.region;

import com.untripical.dto.guideDetails.GuideDetailsResponseDTO;
import com.untripical.dto.place.PlaceRequestDTO;
import com.untripical.dto.place.PlaceResponseDTO;
import com.untripical.enums.RegionType;
import lombok.Value;

import java.util.List;

@Value
public class RegionResponseDTO {
    Long id;
    RegionType type;
    String closestBigCity;
    List<PlaceResponseDTO> places;
    List<GuideDetailsResponseDTO> guides;
}
