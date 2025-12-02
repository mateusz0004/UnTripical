package com.untripical.controller;

import com.untripical.dto.region.RegionRequestDTO;
import com.untripical.dto.region.RegionResponseDTO;
import com.untripical.enums.RegionType;
import com.untripical.service.RegionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
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
    public ResponseEntity<RegionResponseDTO> getRegionById(@PathVariable Long regionId) {
        return ResponseEntity.ok(regionService.getRegionById(regionId));
    }

    @GetMapping
    public ResponseEntity<List<RegionResponseDTO>> getAllRegions() {
        return ResponseEntity.ok(regionService.getAllRegions());
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<RegionResponseDTO> getRegionByType(@PathVariable RegionType type) {
        return ResponseEntity.ok(regionService.getByRegionType(type));
    }

    @GetMapping("/city/{closestBigCity}")
    public ResponseEntity<RegionResponseDTO> getRegionByClosestBigCity(@PathVariable String closestBigCity) {
        return ResponseEntity.ok(regionService.getByClosestBigCity(closestBigCity));
    }

    @PostMapping
    public ResponseEntity<RegionResponseDTO> addRegion(@Valid @RequestBody RegionRequestDTO dto) {
        return ResponseEntity.ok(regionService.addRegion(dto));
    }

    @DeleteMapping("/{regionId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RegionResponseDTO> deleteRegion(@PathVariable Long regionId) {
        regionService.deleteRegion(regionId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{regionId}")
    public ResponseEntity<RegionResponseDTO> updateRegion(@PathVariable Long regionId, @RequestBody RegionRequestDTO dto) {
        return ResponseEntity.ok(regionService.updateRegion(regionId, dto));
    }
}
