package com.untripical.mapper.tripStop;

import com.untripical.dto.tripStop.TripStopRequestDTO;
import com.untripical.dto.tripStop.TripStopResponseDTO;
import com.untripical.model.TripStop;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TripStopMapper {

    @Mapping(source = "placeId", target = "place.id")
    TripStop toEntity(TripStopRequestDTO dto);


    @Mapping(source = "place.id", target = "placeId")
   // @Mapping(source = "tripPlan.id", target = "tripPlanId")
    TripStopResponseDTO toResponse(TripStop entity);
}
