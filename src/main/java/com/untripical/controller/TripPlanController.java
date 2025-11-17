package com.untripical.controller;

import com.untripical.dto.tripPlan.TripPlanRequestDTO;
import com.untripical.dto.tripPlan.TripPlanResponseDTO;
import com.untripical.model.TripPlan;
import com.untripical.service.TripPlanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/plan")
@RestController
public class TripPlanController {

    @Autowired
    TripPlanService tripPlanService;


    @PostMapping
    public ResponseEntity<TripPlanResponseDTO> addTripPlan(@RequestBody TripPlanRequestDTO dto){
        return ResponseEntity.ok(tripPlanService.addTripPlan(dto));
    }

    @GetMapping("/{name}")
    public ResponseEntity<TripPlanResponseDTO> getTripPlanById (@PathVariable String name){
        return ResponseEntity.ok(tripPlanService.getTripPlanByName(name));
    }

    @GetMapping("/all")
    public ResponseEntity<List<TripPlanResponseDTO>> getAllTripPlans (){
        return ResponseEntity.ok(tripPlanService.getAllTripPlans());
    }
}
