package com.untripical.repository;

import com.untripical.model.TripStop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TripStopRepository extends JpaRepository<TripStop, Long> {
    List<TripStop> findByTripPlan_Id(Long tripPlanId);
    Optional<TripStop> findByOrderIndexAndTripPlan_User_IdAndTripPlan_Id(Long orderIndex, Long userId, Long tripPlanId);
    List<TripStop> findAllByTripPlan_IdAndTripPlan_User_Id(Long tripPlanId, Long userId);
}
