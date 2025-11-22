package com.untripical.controller;

import com.untripical.dto.guideDetails.GuideDetailsResponseDTO;
import com.untripical.dto.guideDetails.GuideDetailsUpdateResponseWithTokenDTO;
import com.untripical.dto.userDto.GuideDetailsOrUserRequest;
import com.untripical.enums.Specialisation;
import com.untripical.guideDetailsOrUser.GuideDetailsOrUserUpdateDTO;
import com.untripical.service.GuideDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/guide")
@RestController
public class GuideDetailsController {
    @Autowired
    private GuideDetailsService guideDetailsService;

    @GetMapping("/{id}")
    public ResponseEntity<GuideDetailsResponseDTO> getGuideDetailsById(@PathVariable Long id) {
        return ResponseEntity.ok(guideDetailsService.getGuideDetailsById(id));
    }

    @GetMapping("/name/{guideName}")
    public ResponseEntity<GuideDetailsResponseDTO> getGuideDetailsByGuideName(
            @PathVariable String guideName) {
        return ResponseEntity.ok(guideDetailsService.getGuideDetailsByGuideName(guideName));
    }

    @GetMapping("/specialisation/{specialisation}")
    public ResponseEntity<List<GuideDetailsResponseDTO>> getGuideDetailsBySpecialisation(
            @PathVariable Specialisation specialisation) {
        return ResponseEntity.ok(guideDetailsService.getGuideDetailsBySpecialisation(specialisation));
    }

    @PostMapping("/register")
    public ResponseEntity<GuideDetailsResponseDTO> registerGuideDetails(@RequestBody GuideDetailsOrUserRequest dto){
        return ResponseEntity.ok(guideDetailsService.guideRegister(dto));
    }

    @PutMapping
    public ResponseEntity<GuideDetailsUpdateResponseWithTokenDTO> updateGuideDetails(@RequestBody GuideDetailsOrUserUpdateDTO dto) {
        return ResponseEntity.ok(guideDetailsService.updateGuideDetails(dto));
    }

    @DeleteMapping
    public ResponseEntity<GuideDetailsResponseDTO> deleteGuideDetails(){
        guideDetailsService.deleteGuideDetails();
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<GuideDetailsResponseDTO> deleteGuideDetailsWithIdByAdmin(@PathVariable Long id){
        guideDetailsService.deleteGuideDetailsWithIdByAdmin(id);
        return ResponseEntity.noContent().build();
    }
}