package com.untripical.mapper.guideDetails;

import com.untripical.dto.guideDetails.GuideDetailsRequestDTO;
import com.untripical.dto.guideDetails.GuideDetailsResponseDTO;
import com.untripical.dto.guideDetails.GuideDetailsUpdateDTO;
import com.untripical.model.GuideDetails;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface GuideDetailsMapper {
    @Mapping(source = "regionId", target = "region.id")
    GuideDetails toEntity(GuideDetailsRequestDTO dto);

    @Mapping(source = "region.id", target = "regionId")
    @Mapping(source = "user.username", target = "username")
    GuideDetailsResponseDTO toResponse(GuideDetails entity);

    @Mapping(source = "regionId", target = "region.id")
    GuideDetails toEntity(GuideDetailsUpdateDTO dto);



}
