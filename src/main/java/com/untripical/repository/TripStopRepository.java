package com.untripical.repository;

import com.untripical.model.TripStop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

@Repository
public interface TripStopRepository extends JpaRepository<TripStop, Long> {
    List<TripStop> findByTripPlanId(Long tripPlanId);

}
