package com.untripical.mapper.announcement;

import com.untripical.dto.announcement.AnnouncementRequestDTO;
import com.untripical.dto.announcement.AnnouncementResponseDTO;
import com.untripical.mapper.guideAnnouncementTable.GuideAnnouncementMapper;
import com.untripical.model.Announcement;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {GuideAnnouncementMapper.class})
public interface AnnouncementMapper {

    @Mapping(source = "placeId", target = "place.id")
    Announcement toEntity(AnnouncementRequestDTO dto);

    @Mapping(source = "place.id", target = "placeId")
    AnnouncementResponseDTO toResponse(Announcement entity);
}
