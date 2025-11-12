package com.untripical.mapper.tripPlan;

import com.untripical.dto.tripPlan.TripPlanRequestDTO;
import com.untripical.dto.tripPlan.TripPlanResponseDTO;
import com.untripical.model.TripPlan;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TripPlanMapper {
    @Mapping(source = "userId", target = "user.id")
    TripPlan toEntity(TripPlanRequestDTO dto);

    @Mapping(source = "user.id", target = "userId")
    TripPlanResponseDTO toResponse(TripPlan entity);
}
