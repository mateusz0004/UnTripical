package com.untripical.controller;

import com.untripical.dto.place.PlaceRequestDTO;
import com.untripical.dto.place.PlaceResponseDTO;
import com.untripical.dto.place.PlaceUpdateDTO;
import com.untripical.enums.PlaceType;
import com.untripical.enums.RegionType;
import com.untripical.enums.VerificationStatus;
import com.untripical.service.PlaceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/place")
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

    @GetMapping("/popular")
    public ResponseEntity<List<PlaceResponseDTO>> getTheMostPopularPlaces(){
        return ResponseEntity.ok(placeService.getTheMostPopularPlaces());
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

    @GetMapping("/avg-opinion-higher-than/{avgOpinion}")
    public List<PlaceResponseDTO> getPlaceWithAvgOpinionHigherThan (@PathVariable Long avgOpinion){
        return placeService.getPlaceWithAvgOpinionHigherThan(avgOpinion);
    }

    @GetMapping("/amount-of-opinion-higher-than/{minReviews}")
    public List<PlaceResponseDTO> getPlaceWithAmountOfOpinionHigherThan (@PathVariable Long minReviews){
        return placeService.getPlaceWithAmountOfOpinionsHigherThan(minReviews);
    }

    @GetMapping("/created-by/waiting-for-approval/{createdBy}")
    List<PlaceResponseDTO> listOfPlaceWithStatusWaitingForApproval (@PathVariable Long createdBy){
        return placeService.getPlacesAddedByUserAndWaitingForApproval(createdBy);
    }

    @GetMapping("/waiting-for-approval")
    List<PlaceResponseDTO> listOfPlaceWithStatusWaitingForApprovalAndCreatedBy (){
        return placeService.listOfPlaceWithStatusWaitingForApproval();
    }

    @GetMapping("/similar/{placeId}")
    List<PlaceResponseDTO> getSimilarPlacesToPlaceWithId (@PathVariable Long placeId){
        return placeService.getSimilarPlacesToPlaceWithId(placeId);
    }

    @GetMapping("/average-place-rating/{placeId}")
    double getAveragePlaceRating(@PathVariable Long placeId){
        return placeService.getAveragePlaceRating(placeId);
    }

    @GetMapping("/by-place-type/{placeType}")
    List<PlaceResponseDTO> getPlacesByPlaceType (@PathVariable PlaceType placeType){
        return placeService.getPlaceByPlaceType(placeType);
    }

    @GetMapping("/with-region-and-avgOpinion/{regionId}/{avgOpinion}")
    List<PlaceResponseDTO> getPlacesByRegionAndAvgOpinionHigherThan (@PathVariable Long regionId, @PathVariable double avgOpinion){
        return placeService.getPlaceByRegionAndAverageRatingHigherThan(regionId, avgOpinion);
    }

    @GetMapping("/with-city-and-avgOpinion/{city}/{avgOpinion}")
    List<PlaceResponseDTO> getPlacesByCityAndAvgOpinionHigherThan (@PathVariable String city, @PathVariable double avgOpinion){
        return placeService.getPlaceByCityAndAverageRatingHigherThan(city, avgOpinion);
    }
}
