package com.untripical.repository;

import com.untripical.enums.RegionType;
import com.untripical.model.Region;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RegionRepository extends JpaRepository<Region, Long> {
    Optional<Region> findById(Long id);
    Optional<Region> findByType(RegionType type);
    Optional<Region> findByClosestBigCity(String closestBigCity);
    Optional<Region> findByTypeAndClosestBigCity(RegionType type, String closestBigCity);
}
