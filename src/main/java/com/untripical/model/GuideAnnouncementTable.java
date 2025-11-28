package com.untripical.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Date;


@Table(name = "guide_announcement_tables")
@Entity
@Data
public class GuideAnnouncementTable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;


    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "announcement_id")
    private Announcement announcement;

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "guide_details_id")
    private GuideDetails guideDetails;
}
