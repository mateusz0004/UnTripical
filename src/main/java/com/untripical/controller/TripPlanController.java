package com.untripical.controller;

import com.untripical.dto.tripPlan.TripPlanRequestDTO;
import com.untripical.dto.tripPlan.TripPlanResponseDTO;
import com.untripical.model.TripPlan;
import com.untripical.service.TripPlanService;
import jakarta.persistence.PrePersist;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Date;
import java.util.List;

@RequestMapping("/plan")
@RestController
public class TripPlanController {

    @Autowired
    TripPlanService tripPlanService;


    @PostMapping
    public ResponseEntity<TripPlanResponseDTO> addTripPlan(@RequestBody TripPlanRequestDTO dto){
        return ResponseEntity.ok(tripPlanService.addTripPlan(dto));
    }

    @GetMapping("/{name}")
    public ResponseEntity<TripPlanResponseDTO> getTripPlanByName (@PathVariable String name){
        return ResponseEntity.ok(tripPlanService.getTripPlanByName(name));
    }

    @GetMapping("/all")
    public ResponseEntity<List<TripPlanResponseDTO>> getAllTripPlans (){
        return ResponseEntity.ok(tripPlanService.getAllTripPlans());
    }

    @DeleteMapping("/{name}")
    public ResponseEntity<TripPlanResponseDTO> deleteTripPlan (@PathVariable String name){
        tripPlanService.deleteTripPlan(name);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/date")
    public ResponseEntity<List<TripPlanResponseDTO>> getTripPlanByDate(@RequestBody TripPlanRequestDTO dto){
        return ResponseEntity.ok(tripPlanService.getTripPlanByDate(dto.getDayWhenTripPlanIsStarting()));
     }

    @PutMapping("/update/{name}")
    public ResponseEntity<TripPlanResponseDTO> updateTripPlan(@RequestBody TripPlanRequestDTO dto, @PathVariable String name){
        return ResponseEntity.ok(tripPlanService.updateTripPlan(dto, name));

    }
}
