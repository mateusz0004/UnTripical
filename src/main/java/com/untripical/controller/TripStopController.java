package com.untripical.controller;

import com.untripical.dto.tripStop.TripStopRequestDTO;
import com.untripical.dto.tripStop.TripStopResponseDTO;
import com.untripical.dto.tripStop.TripStopUpdateRequestDTO;
import com.untripical.service.TripStopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/tripStop")
@RestController
public class TripStopController {

    @Autowired
    private TripStopService tripStopService;

    @PostMapping
    public ResponseEntity<TripStopResponseDTO> addTripStop (@RequestBody TripStopRequestDTO dto){
        return ResponseEntity.ok(tripStopService.addTripStop(dto));
    }

    @DeleteMapping("/{orderIndex}/{tripPlanId}")
    public ResponseEntity<TripStopResponseDTO> deleteTripStop (@PathVariable Long orderIndex, @PathVariable Long tripPlanId) throws Exception {
        tripStopService.deleteTripStop(orderIndex, tripPlanId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{orderIndex}/{tripPlanId}")
    public ResponseEntity<TripStopResponseDTO> updateTripStop(@RequestBody TripStopUpdateRequestDTO dto, @PathVariable Long orderIndex, @PathVariable Long tripPlanId){
        return ResponseEntity.ok(tripStopService.updateTripStop(dto,orderIndex, tripPlanId));
    }

    @GetMapping("/{tripPlanId}")
    public ResponseEntity<List<TripStopResponseDTO>>getAllTripStopsWithPlanId(@PathVariable Long tripPlanId){
        return ResponseEntity.ok(tripStopService.findAllTripStopsWithPlanId(tripPlanId));
    }
}
