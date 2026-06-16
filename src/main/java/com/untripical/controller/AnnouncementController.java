package com.untripical.controller;

import com.untripical.dto.announcement.AnnouncementDateRequestDTO;
import com.untripical.dto.announcement.AnnouncementRequestDTO;
import com.untripical.dto.announcement.AnnouncementResponseDTO;
import com.untripical.dto.announcement.AnnouncementUpdateDTO;
import com.untripical.enums.AnnouncementType;
import com.untripical.model.Announcement;
import com.untripical.service.AnnouncementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RequestMapping("/announcement")
@RestController
public class AnnouncementController {

    @Autowired
    AnnouncementService announcementService;

    @PostMapping("/add")
    public ResponseEntity<AnnouncementResponseDTO> addAnnouncement(@RequestBody AnnouncementRequestDTO dto){
        return ResponseEntity.ok(announcementService.addAnnouncement(dto));
    }

    @DeleteMapping("/delete/{name}")
    public ResponseEntity<AnnouncementResponseDTO> deleteAnnouncement (@PathVariable String name){
        announcementService.deleteAnnouncementByNameOnlyIfYouAreGuideOrAdmin(name);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/update/{name}")
    public ResponseEntity<AnnouncementResponseDTO> updateDescription (@PathVariable String name, @RequestBody AnnouncementUpdateDTO dto){
        return ResponseEntity.ok(announcementService.updateAnnouncement(dto, name));
    }

    @GetMapping("/all")
    public ResponseEntity<List<AnnouncementResponseDTO>> getAllAnnouncements(){
        return ResponseEntity.ok(announcementService.getAllAnnouncements());
    }

    @GetMapping("/type/{announcementType}")
    public ResponseEntity<List<AnnouncementResponseDTO>> getAnnouncementsByAnnouncementType (@PathVariable AnnouncementType announcementType){
        return ResponseEntity.ok(announcementService.getAnnouncementsByAnnouncementType(announcementType));
    }

    @GetMapping("/date")
    public ResponseEntity<List<AnnouncementResponseDTO>> getAnnouncementsByDate (@RequestBody AnnouncementDateRequestDTO dto){
        return ResponseEntity.ok(announcementService.getAnnouncementsByDate(dto.getDate()));
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<AnnouncementResponseDTO> getAnnouncementById(@PathVariable Long id){
        return ResponseEntity.ok(announcementService.getAnnouncementById(id));
    }

    @GetMapping("/order_by_dsc")
    public ResponseEntity<List<AnnouncementResponseDTO>> getAnnouncementsOrderByDsc(){
        return ResponseEntity.ok(announcementService.getAnnouncementsOrderByDsc());
    }

    @GetMapping("/order_by_asc")
    public ResponseEntity<List<AnnouncementResponseDTO>> getAnnouncementsOrderByAsc(){
        return ResponseEntity.ok(announcementService.getAnnouncementsOrderByAsc());
    }

    @GetMapping("/location/{locationInfo}")
    public ResponseEntity<List<AnnouncementResponseDTO>> getAnnouncementsByLocationInfo(@PathVariable String locationInfo){
        return ResponseEntity.ok(announcementService.getAnnouncementsByLocationInfo(locationInfo));
    }

    @GetMapping("/placeId/{placeId}")
    public ResponseEntity<List<AnnouncementResponseDTO>> getAnnouncementsByPlaceId (@PathVariable Long placeId){
        return ResponseEntity.ok(announcementService.getAnnouncementsByPlaceId(placeId));
    }

}
