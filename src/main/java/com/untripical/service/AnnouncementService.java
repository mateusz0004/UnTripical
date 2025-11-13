package com.untripical.service;

import com.untripical.dto.announcement.AnnouncementResponseDTO;
import com.untripical.mapper.announcement.AnnouncementMapper;
import com.untripical.repository.AnnouncementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AnnouncementService {
    @Autowired
    private AnnouncementRepository repository;
    private AnnouncementMapper mapper;

   // public AnnouncementResponseDTO get
}
