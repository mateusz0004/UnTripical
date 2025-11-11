package com.untripical.repository;

import com.untripical.model.GuideAnnouncementTable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GuideAnnoucementTableRepository extends JpaRepository<GuideAnnouncementTable, Long> {
}
