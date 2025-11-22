package com.untripical.service;

import com.untripical.dto.announcement.AnnouncementRequestDTO;
import com.untripical.dto.announcement.AnnouncementResponseDTO;
import com.untripical.exception.announcement.AnnouncementDoesNotExist;
import com.untripical.exception.announcement.ListOfAnnouncementDoesNotExist;
import com.untripical.exception.place.PlaceDoesNotExist;
import com.untripical.exception.user.UserDoesNotExist;
import com.untripical.mapper.announcement.AnnouncementMapper;
import com.untripical.model.Announcement;
import com.untripical.model.GuideAnnouncementTable;
import com.untripical.model.User;
import com.untripical.repository.AnnouncementRepository;
import com.untripical.repository.PlaceRepository;
import com.untripical.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class AnnouncementService {
    @Autowired
    private AnnouncementRepository announcementRepository;
    @Autowired
    private AnnouncementMapper announcementMapper;
    @Autowired
    private PlaceRepository placeRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;

    public AnnouncementResponseDTO getAnnouncementById(Long id){
        Announcement announcement = announcementRepository.findById(id).
                orElseThrow(()->new AnnouncementDoesNotExist("This announcement does not exist"));
        return announcementMapper.toResponse(announcement);
    }
    public List<AnnouncementResponseDTO> getAllAnnouncements(){
        return announcementRepository.findAll().stream()
                .map(announcementMapper::toResponse)
                .collect(Collectors.toList());
    }
    public List<AnnouncementResponseDTO> getAnnouncementsOrderByAsc (){
        List<Announcement> announcements = announcementRepository.findAllByOrderByPriceAsc()
                .orElseThrow(()-> new ListOfAnnouncementDoesNotExist("This announcements does not exist"));
        return announcements.stream()
                .map(announcementMapper::toResponse)
                .collect(Collectors.toList());
    }
    public List<AnnouncementResponseDTO> getAnnouncementsOrderByDsc (){
        List<Announcement> announcements = announcementRepository.findAllByOrderByPriceDesc()
                .orElseThrow(()-> new ListOfAnnouncementDoesNotExist("This announcements does not exist"));
        return announcements.stream()
                .map(announcementMapper::toResponse)
                .collect(Collectors.toList());
    }
    public AnnouncementResponseDTO addAnnouncement(AnnouncementRequestDTO dto){
        Announcement announcement = announcementMapper.toEntity(dto);
        announcement.setPlace(placeRepository.findById(dto.getPlaceId())
                .orElseThrow(()-> new PlaceDoesNotExist("This place does not exist")));

        Long currentGuideId = userService.getCurrentUser().getId();
        User currentGuide = userRepository.findById(currentGuideId)
                .orElseThrow(()-> new UserDoesNotExist("This user does not exist"));
        GuideAnnouncementTable table = new GuideAnnouncementTable();
        //table.setGuideDetails(currentGuide);
        /// /// najpierw zrobić klase currentGuide


        return null;
    }


    /// // toDo:: zrobic wyszukiwanie po nazwie miejsca, jak już będę miał service Place'a
}
