package com.untripical.repository;

import com.untripical.model.TripPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface TripPlanRepository extends JpaRepository<TripPlan, Long> {
    Optional<List<TripPlan>> findByUser_Username(String username);
    Optional<TripPlan> findByNameAndUser_Username(String planName, String username);
    Optional<TripPlan> findByIdAndUser_Username(Long planId, String username);
    Optional<TripPlan> findByName(String name);
    Optional<TripPlan> findByNameAndIsActiveTrueAndUser_Username(String name, String username);
    Optional<List<TripPlan>> findByAssignedAtAndUser_Username(Date date, String username);
}
