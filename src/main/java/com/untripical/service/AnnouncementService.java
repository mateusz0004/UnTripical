package com.untripical.service;

import com.untripical.dto.announcement.AnnouncementRequestDTO;
import com.untripical.dto.announcement.AnnouncementResponseDTO;
import com.untripical.dto.announcement.AnnouncementUpdateDTO;
import com.untripical.enums.AnnouncementType;
import com.untripical.exception.announcement.AnnouncementDoesNotExist;
import com.untripical.exception.announcement.ListOfAnnouncementDoesNotExist;
import com.untripical.exception.place.PlaceDoesNotExist;
import com.untripical.exception.user.UserDoesNotExist;
import com.untripical.mapper.announcement.AnnouncementMapper;
import com.untripical.mapper.guideAnnouncementTable.GuideAnnouncementMapper;
import com.untripical.model.Announcement;
import com.untripical.model.GuideAnnouncementTable;
import com.untripical.model.User;
import com.untripical.repository.*;
import com.untripical.enums.UserRole;
import com.untripical.exception.UnauthorizedAccess.UnauthorizedAccess;
import com.untripical.exception.announcement.AnnouncementAlreadyExists;
import com.untripical.exception.guideAnnouncementTable.GuideAnnouncementTableDoesNotExist;
import com.untripical.exception.user.IncorrectRoleTypeException;
import com.untripical.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class AnnouncementService {

    @Autowired
    private AnnouncementMapper announcementMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AnnouncementRepository announcementRepository;

    @Autowired
    private GuideAnnouncementTableRepository guideAnnouncementTableRepository;

    @Autowired
    private GuideDetailsRepository guideDetailsRepository;

    @Autowired
    private PlaceRepository placeRepository;

    @Autowired
    private GuideAnnouncementMapper guideAnnouncementMapper;

    public AnnouncementResponseDTO addAnnouncement(AnnouncementRequestDTO dto) {

        User user = validation();

        if (!user.getUserRole().equals(UserRole.GUIDE)) {
            throw new IncorrectRoleTypeException("You're not a guide - you cannot add announcement");
        }

        if (announcementRepository.existsByNameOfJourney(dto.getNameOfJourney())) {
            throw new AnnouncementAlreadyExists("Announcement " + dto.getNameOfJourney() + " already exists");
        }

        Place place = placeRepository.findById(dto.getPlaceId())
                .orElseThrow(()-> new PlaceDoesNotExist("Place with ID " + dto.getPlaceId() + " does not exist"));

        Announcement announcement = announcementMapper.toEntity(dto);
        announcement.setPlace(place);
        place.getAnnouncements().add(announcement);
        Announcement saved = announcementRepository.save(announcement);


        GuideDetails guideDetails = guideDetailsRepository.findById(user.getId())
                .orElseThrow(() -> new UserDoesNotExist("User with ID " + user.getId() + "does not exist"));

        GuideAnnouncementTable guideAnnouncementTable = new GuideAnnouncementTable();
        guideAnnouncementTable.setAnnouncement(saved);
        guideAnnouncementTable.setGuideDetails(guideDetails);

        guideAnnouncementTableRepository.save(guideAnnouncementTable);

        return announcementMapper.toResponse(saved);
    }

    public User validation(){

        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UserDoesNotExist("User " + username + " does not exist"));
    }

    public void deleteAnnouncementByNameOnlyIfYouAreGuideOrAdmin(String nameOfJourney) {

        User user = validation();

        if (user.getUserRole().equals(UserRole.USER)) {
            throw new IncorrectRoleTypeException("You're not a guide or admin - you cannot remove announcement");
        }

        if (user.getUserRole().equals(UserRole.GUIDE)) {

            Announcement announcement = announcementRepository.findByNameOfJourney(nameOfJourney)
                    .orElseThrow(() -> new AnnouncementDoesNotExist("User " + user.getUsername() + " does not exist"));

            GuideAnnouncementTable announcementInTable = guideAnnouncementTableRepository.findByAnnouncement_Id(announcement.getId())
                    .orElseThrow(() -> new GuideAnnouncementTableDoesNotExist("There is no " + announcement.getNameOfJourney() + " in GuideAnnouncementTable"));

            if (!announcementInTable.getGuideDetails().getId().equals(user.getId())) {
                throw new UnauthorizedAccess("You are not the owner of this announcement");
            }

            announcement.setIsActive(false);
        }
    }

    public AnnouncementResponseDTO getAnnouncementById(Long id) {
        Announcement announcement = announcementRepository.findById(id).
                orElseThrow(() -> new AnnouncementDoesNotExist("This announcement does not exist"));
        if(!announcement.getIsActive()){
            throw new AnnouncementDoesNotExist("Announcement with ID: " + announcement.getId() + " does not exist");
        }
        return announcementMapper.toResponse(announcement);
    }

    public List<AnnouncementResponseDTO> getAllAnnouncements() {
        return announcementRepository.findAll().stream()
                .filter(re -> re.getIsActive() == true)
                .map(announcementMapper::toResponse)
                .collect(Collectors.toList());
    }

    public List<AnnouncementResponseDTO> getAnnouncementsOrderByAsc() {
        List<Announcement> announcements = announcementRepository.findAllByOrderByPriceAsc()
                .orElseThrow(() -> new ListOfAnnouncementDoesNotExist("This announcements does not exist"));
        return announcements.stream()
                .filter(Announcement::getIsActive)
                .map(announcementMapper::toResponse)
                .collect(Collectors.toList());
    }

    public List<AnnouncementResponseDTO> getAnnouncementsOrderByDsc() {
        List<Announcement> announcements = announcementRepository.findAllByOrderByPriceDesc()
                .orElseThrow(() -> new ListOfAnnouncementDoesNotExist("This announcements does not exist"));
        return announcements.stream()
                .filter(Announcement::getIsActive)
                .map(announcementMapper::toResponse)
                .collect(Collectors.toList());
    }

    public List<AnnouncementResponseDTO> getAnnouncementsByLocationInfo(String locationInfo) {
        List<Announcement> announcements = announcementRepository.findAllByLocationInfo(locationInfo)
                .orElseThrow(() -> new ListOfAnnouncementDoesNotExist("These announcements do not exist"));
        return announcements.stream()
                .filter(Announcement::getIsActive)
                .map(announcementMapper::toResponse)
                .collect(Collectors.toList());
    }

    public AnnouncementResponseDTO updateDescription(AnnouncementUpdateDTO dto, String nameOfJourney){

        User user = validation();

        Announcement announcement = announcementRepository.findByNameOfJourney(nameOfJourney)
                .orElseThrow(() -> new AnnouncementDoesNotExist("Announcement " + nameOfJourney + " does not exist"));

        GuideAnnouncementTable announcementTable = guideAnnouncementTableRepository.findByAnnouncement_Id(announcement.getId()).
                orElseThrow(() -> new AnnouncementDoesNotExist("This announcement does not exist"));

        if(!announcement.getIsActive()){
            throw new AnnouncementDoesNotExist("This announcement does not exist");
        }

        if(!user.getId().equals(announcementTable.getGuideDetails().getId())){
            throw new UnauthorizedAccess("You are not the owner of this announcement - you cannot update it");
        }

        if(dto.getNameOfJourney() != null){
            Announcement announcement1 = announcementRepository.findByNameOfJourney(dto.getNameOfJourney())
                    .orElseThrow();

            if(announcement1.getIsActive()){
                throw new AnnouncementAlreadyExists("Announcement with this name already exist");
            }

            announcement.setNameOfJourney(dto.getNameOfJourney());
        }

        if(dto.getDescription() != null){
            announcement.setDescription(dto.getDescription());
        }

        if(dto.getAnnouncementType() != null){
            announcement.setAnnouncementType(dto.getAnnouncementType());
        }

        if(dto.getDate() != null){
            announcement.setDate(dto.getDate());
        }

        if(dto.getPrice() != null){
            announcement.setPrice(dto.getPrice());
        }

        if(dto.getLocationInfo() != null){
            announcement.setLocationInfo(dto.getLocationInfo());
        }

        if(dto.getMaxParticipants() != null){
            announcement.setMaxParticipants(dto.getMaxParticipants());
        }

        announcementRepository.save(announcement);

        return announcementMapper.toResponse(announcement);
    }

    public List<AnnouncementResponseDTO> getAnnouncementsByAnnouncementType (AnnouncementType announcementType){

        List<Announcement> announcements = announcementRepository.findAllByAnnouncementType(announcementType)
                .orElseThrow(() -> new ListOfAnnouncementDoesNotExist("These announcements do not exist"));
        return announcements.stream()
                .filter(Announcement::getIsActive)
                .map(announcementMapper::toResponse)
                .collect(Collectors.toList());
    }

    public List<AnnouncementResponseDTO> getAnnouncementsByDate(LocalDate date){

        List<Announcement> announcements = announcementRepository.findAllByDate(date)
                .orElseThrow(() -> new ListOfAnnouncementDoesNotExist("These announcements do not exist"));
        return   announcements.stream()
                .filter(Announcement::getIsActive)
                .map(announcementMapper::toResponse)
                .collect(Collectors.toList());
    }

    public List<AnnouncementResponseDTO> getAnnouncementsByPlaceId (Long placeId){

        List<Announcement> announcements = announcementRepository.findAllByPlaceId(placeId)
                .orElseThrow(() -> new ListOfAnnouncementDoesNotExist("List of announcements for this place does not exist"));

        return announcements.stream()
                .filter(Announcement::getIsActive)
                .map(announcementMapper::toResponse)
                .collect(Collectors.toList());
    }

}