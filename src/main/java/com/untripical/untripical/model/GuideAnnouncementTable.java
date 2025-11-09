package com.untripical.untripical.model;

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

    @NotBlank(message = "name must not be blank")
    @Column(name = "name")
    private String name;

    @NotNull(message = "date must not be null")
    @Column(name = "date")
    private Date date;

    @Min(0)
    @NotNull(message = "totalDistance must not be null")
    @Column(name = "total_distance")
    private Double totalDistance;

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "announcement_id")
    private Announcement announcement;

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "guide_details_id")
    private GuideDetails guideDetails;
}
