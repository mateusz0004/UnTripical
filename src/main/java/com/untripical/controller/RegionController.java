package com.untripical.controller;

import com.untripical.dto.region.RegionRequestDTO;
import com.untripical.dto.region.RegionResponseDTO;
import com.untripical.enums.RegionType;
import com.untripical.service.RegionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Role;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/region")
@RestController
public class RegionController {
    @Autowired
    private RegionService regionService;

    @GetMapping("/{regionId}")
    ResponseEntity<RegionResponseDTO> getRegionById(@PathVariable Long regionId) {
        return ResponseEntity.ok(regionService.getRegionById(regionId));
    }

    @GetMapping
    ResponseEntity<List<RegionResponseDTO>> getAllRegions() {
        return ResponseEntity.ok(regionService.getAllRegions());
    }

    @GetMapping("/type/{type}")
    ResponseEntity<RegionResponseDTO> getRegionByType(@PathVariable RegionType type) {
        return ResponseEntity.ok(regionService.getByRegionType(type));
    }

    @GetMapping("/city/{closestBigCity}")
    ResponseEntity<RegionResponseDTO> getRegionByClosestBigCity(@PathVariable String closestBigCity) {
        return ResponseEntity.ok(regionService.getByClosestBigCity(closestBigCity));
    }

    @PostMapping
    ResponseEntity<RegionResponseDTO> addRegion(@RequestBody RegionRequestDTO dto) {
        return ResponseEntity.ok(regionService.addRegion(dto));
    }

    @DeleteMapping("/{regionId}")
    @PreAuthorize("hasRole('ADMIN')")
    ResponseEntity<RegionResponseDTO> deleteRegion(@PathVariable Long regionId) {
        regionService.deleteRegion(regionId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    ResponseEntity<RegionResponseDTO> updateRegion(@PathVariable Long regionId, @PathVariable RegionRequestDTO dto) {
        return ResponseEntity.ok(regionService.updateRegion(regionId, dto));
    }
}
