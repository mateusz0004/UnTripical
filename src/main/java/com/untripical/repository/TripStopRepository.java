package com.untripical.repository;

import com.untripical.model.TripStop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TripStopRepository extends JpaRepository<TripStop, Long> {
    List<TripStop> findByTripPlanId(Long tripPlanId);
    @Query("SELECT COALESCE(MAX(ts.orderIndex), 0) FROM TripStop ts WHERE ts.tripPlan.id = :tripPlanId")
    int findMaxOrderIndexByTripPlanId(Long tripPlanId);
    TripStop findByName(String name);
    boolean existsByNameAndTripPlan_Id(String name, Long tripPlanId);

}
