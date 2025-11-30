package com.untripical.repository;

import com.untripical.model.GuideAnnouncementTable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GuideAnnouncementTableRepository extends JpaRepository<GuideAnnouncementTable, Long> {
    Optional<GuideAnnouncementTable> findByAnnouncement_Id(Long announcementId);
}
