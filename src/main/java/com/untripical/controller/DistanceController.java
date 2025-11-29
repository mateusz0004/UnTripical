package com.untripical.controller;

import com.untripical.service.DistanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DistanceController {

    @Autowired
    private DistanceService distanceService;

    @GetMapping("/distance")
    public String getDistance(
            @RequestParam String origin,
            @RequestParam String destination
    ) {
        try {
            double distanceKm = distanceService.getDistanceInKm(origin, destination);
            return String.format("Odległość między '%s' a '%s' wynosi %.2f km",
                    origin, destination, distanceKm);
        } catch (Exception e) {
            return "Błąd: " + e.getMessage();
        }
    }
}