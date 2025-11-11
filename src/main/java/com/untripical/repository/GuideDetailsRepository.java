package com.untripical.repository;

import com.untripical.model.GuideDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GuideDetailsRepository extends JpaRepository<GuideDetails, Long> {
}
