package com.untripical.repository;

import com.untripical.enums.AnnouncementType;
import com.untripical.model.Announcement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface AnnouncementRepository extends JpaRepository<Announcement, Long> {
    Optional<Announcement> findByNameOfJourney(String NameOfJourney);
    Optional<List<Announcement>> findAllByOrderByPriceAsc();
    Optional<List<Announcement>> findAllByOrderByPriceDesc();
    boolean existsByNameOfJourney(String name);
    Optional<List<Announcement>> findAllByLocationInfo(String locationInfo);
    Optional<List<Announcement>> findAllByAnnouncementType(AnnouncementType announcementType);
    Optional<List<Announcement>> findAllByDate(LocalDate date);
    Optional<List<Announcement>> findAllByPlaceId (Long id);

}
