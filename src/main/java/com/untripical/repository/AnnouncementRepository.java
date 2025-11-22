package com.untripical.repository;

import com.untripical.enums.AnnouncementType;
import com.untripical.model.Announcement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AnnouncementRepository extends JpaRepository<Announcement, Long> {
    Optional<Announcement> findByNameOfJourney(String NameOfJourney);
    Optional<Announcement> findByAnnouncementType(AnnouncementType type);
    Optional<List<Announcement>> findAllByOrderByPriceAsc();
    Optional<List<Announcement>> findAllByOrderByPriceDesc();
}
