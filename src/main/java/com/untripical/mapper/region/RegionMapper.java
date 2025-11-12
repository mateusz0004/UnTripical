package com.untripical.mapper.region;

import com.untripical.dto.region.RegionRequestDTO;
import com.untripical.dto.region.RegionResponseDTO;
import com.untripical.model.Region;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface RegionMapper {
    Region toEntity(RegionRequestDTO dto);
    RegionResponseDTO toResponse(RegionMapper entity);
}
