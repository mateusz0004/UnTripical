package com.untripical.repository;

import com.untripical.enums.Specialisation;
import com.untripical.model.GuideDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GuideDetailsRepository extends JpaRepository<GuideDetails, Long> {
    Optional<GuideDetails>  findById(Long id);
    Optional<List<GuideDetails>> findAllBySpecialisation(Specialisation specialisation);
    Optional<List<GuideDetails>> findAllByClosestBigCity(String closestBigCity);
}
