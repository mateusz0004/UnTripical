package com.untripical.repository;

import com.untripical.dto.place.PlaceResponseDTO;
import com.untripical.enums.PlaceType;
import com.untripical.enums.RegionType;
import com.untripical.enums.VerificationStatus;
import com.untripical.model.Place;
import com.untripical.model.Region;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.OptionalDouble;

@Repository
public interface PlaceRepository extends JpaRepository<Place, Long> {
    Optional<Place> findById(Long id);
    Place findByName(String name);
    List<Place> findAllByRegion_Type(RegionType type);
    List<Place> findAllByRegion_ClosestBigCity(String closestBigCity);
    List<Place> findAllByStatus(VerificationStatus status);
    List<Place> findAllByAvgRatingGreaterThan(double value);
    @Query("""
    SELECT p
    FROM Place p
    JOIN p.reviews r
    WHERE r.isActive = true
    GROUP BY p.id
    HAVING COUNT(r) > :minReviews
""")
    List<Place> findPlacesWithMoreThanReviews(Long minReviews);
    List<Place> findAllByStatusAndUser_Id(VerificationStatus status, Long userId);
    List<Place> findAllByPlaceTypeAndRegion_Id(PlaceType placeType, Long regionId);
    List<Place> findAllByPlaceType(PlaceType placeType);
    List<Place> findAllByAvgRatingGreaterThanAndRegion(double avgRating, Region region);
    List<Place> findAllByAvgRatingGreaterThanAndCity(double avgRating, String nameOfCity);

}
