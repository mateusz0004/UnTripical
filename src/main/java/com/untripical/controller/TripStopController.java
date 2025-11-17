package com.untripical.controller;

import com.untripical.dto.tripStop.TripStopRequestDTO;
import com.untripical.dto.tripStop.TripStopResponseDTO;
import com.untripical.service.TripStopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/tripStop")
@RestController
public class TripStopController {

    @Autowired
    private TripStopService tripStopService;

    @PostMapping
    public ResponseEntity<TripStopResponseDTO> addTripStop (@RequestBody TripStopRequestDTO dto){
        return ResponseEntity.ok(tripStopService.addTripStop(dto));
    }
}
