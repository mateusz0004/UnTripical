package com.untripical.mapper.place;
import com.untripical.dto.guideDetails.GuideDetailsUpdateDTO;
import com.untripical.dto.place.PlaceRequestDTO;
import com.untripical.dto.place.PlaceResponseDTO;
import com.untripical.dto.place.PlaceUpdateDTO;
import com.untripical.model.GuideDetails;
import com.untripical.model.Place;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PlaceMapper {
    @Mapping(source = "regionId", target = "region.id")
    Place toEntity(PlaceRequestDTO dto);

    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "region.id", target = "regionId")
    @Mapping(source = "status", target = "verificationStatus")
    PlaceResponseDTO toResponse(Place entity);

    @Mapping(source = "regionId", target = "region.id")
    Place toEntity(PlaceUpdateDTO dto);
}
