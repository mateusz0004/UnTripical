package com.untripical.repository;

import com.untripical.model.TripStop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TripStopRepository extends JpaRepository<TripStop, Long> {
}
