package com.untripical.mapper.place;
import com.untripical.dto.place.PlaceRequestDTO;
import com.untripical.dto.place.PlaceResponseDTO;
import com.untripical.model.Place;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PlaceMapper {
    @Mapping(source = "userId", target = "user.id")
    @Mapping(source = "regionId", target = "region.id")
    Place toEntity(PlaceRequestDTO dto);

    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "region.id", target = "regionId")
    PlaceResponseDTO toResponse(Place entity);
}
