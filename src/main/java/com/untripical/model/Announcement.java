package com.untripical.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.untripical.enums.AnnouncementType;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Table (name = "announcements")
@Entity
@Data
public class Announcement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotBlank(message = "nameOfJourney must not be blank")
    @Column(name = "name_of_journey")
    private String nameOfJourney;

    @NotBlank(message = "description must not be blank")
    @Column(name = "description")
    private String description;

    @NotNull(message = "announcementType must not be null")
    @Enumerated(EnumType.STRING)
    @Column(name = "announcement_type")
    private AnnouncementType announcementType;

    @NotNull(message = "date must not be null")
    @Column(name = "date")
    private LocalDate date;

    @NotNull(message = "price must not be null")
    @Column(name = "price")
    @Min(0)
    private Double price;

    @NotBlank(message = "locationInfo must not be null")
    @Column(name = "location_info")
    private String locationInfo;

    @NotNull(message = "maxParticipants must not be null")
    @Column(name = "max_participants")
    @Positive
    private Integer maxParticipants;

    @NotNull(message = "isActive must not be null")
    @Column(name = "is_active")
    private Boolean isActive;

    @NotNull(message = "createdAt must not be null")
    @Column(name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @OneToMany(mappedBy = "announcement", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<GuideAnnouncementTable> guideAnnouncementTables;

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "place_id", nullable = true)
    private Place place;

    @PrePersist
    protected void onCreateAndSetIsActiveTrue() {

        this.createdAt = new Date();
        this.isActive = true;
    }
}
