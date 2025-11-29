package com.untripical.controller;

import com.untripical.dto.place.PlaceRequestDTO;
import com.untripical.dto.place.PlaceResponseDTO;
import com.untripical.dto.place.PlaceUpdateDTO;
import com.untripical.enums.RegionType;
import com.untripical.enums.VerificationStatus;
import com.untripical.service.PlaceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/place")
public class PlaceController {

    @Autowired
    private PlaceService placeService;

    @PostMapping
    public ResponseEntity<PlaceResponseDTO> addTripPlace(@RequestBody PlaceRequestDTO dto){
        return ResponseEntity.ok(placeService.addPlaceResponse(dto));
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<PlaceResponseDTO> getTripPlace(@PathVariable Long id){
        return ResponseEntity.ok(placeService.getPlaceById(id));
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<List<PlaceResponseDTO>> getAllByRegionType(@PathVariable RegionType type){
        return ResponseEntity.ok(placeService.getAllByRegionType(type));
    }

    @GetMapping("/closestBigCity/{closestBigCity}")
    public ResponseEntity<List<PlaceResponseDTO>> getAllByClosestBigCity(@PathVariable String closestBigCity){
        return ResponseEntity.ok(placeService.getAllByClosestBigCity(closestBigCity));
    }

    @PutMapping("/{placeId}")
    public ResponseEntity<PlaceResponseDTO> updatePlace(@RequestBody PlaceUpdateDTO dto, @PathVariable Long placeId){
        return ResponseEntity.ok(placeService.updatePlace(dto, placeId));
    }

    @PutMapping("/{verificationStatus}/{placeId}")
    @PreAuthorize("hasRole('GUIDE')")
    public ResponseEntity<PlaceResponseDTO> setVerificationStatusOfPlaceByGuide (@PathVariable VerificationStatus verificationStatus, @PathVariable Long placeId){
        return ResponseEntity.ok(placeService.setVerificationStatusOfPlaceByGuide(placeId, verificationStatus));
    }

    @DeleteMapping("/{placeId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PlaceResponseDTO> deletePlaceById(@PathVariable Long placeId){
        placeService.deletePlaceById(placeId);
        return ResponseEntity.noContent().build();
    }
}
