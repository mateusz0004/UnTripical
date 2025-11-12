package com.untripical.mapper.guideAnnouncementTable;

import com.untripical.dto.guideAnnouncementTable.GuideAnnouncementTableRequestDTO;
import com.untripical.dto.guideAnnouncementTable.GuideAnnouncementTableResponseDTO;
import com.untripical.model.GuideAnnouncementTable;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
@Mapper(componentModel = "spring")
public interface GuideAnnouncementMapper {
    @Mapping(source = "guideDetailsId", target = "guideDetails.id")
    @Mapping(source = "announcementId", target = "announcement.id")
    GuideAnnouncementTable toEntity(GuideAnnouncementTableRequestDTO dto);

    @Mapping(source = "guideDetails.id", target = "guideDetailsId")
    @Mapping(source = "announcement.id", target = "announcementId")
    GuideAnnouncementTableResponseDTO toResponse (GuideAnnouncementTable entity);
}
