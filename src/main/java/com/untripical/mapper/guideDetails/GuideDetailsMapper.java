package com.untripical.mapper.guideDetails;

import com.untripical.dto.guideDetails.GuideDetailsRequestDTO;
import com.untripical.dto.guideDetails.GuideDetailsResponseDTO;
import com.untripical.model.GuideDetails;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface GuideDetailsMapper {
    @Mapping(source = "userId", target = "user.id")
    @Mapping(source = "regionId", target = "region.id")
    GuideDetails toEntity(GuideDetailsRequestDTO dto);

    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "region.id", target = "regionId")
    GuideDetailsResponseDTO toResponse(GuideDetails entity);
}
