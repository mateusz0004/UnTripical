package com.untripical.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.untripical.enums.ExperienceLevel;
import com.untripical.enums.Specialisation;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
@Table(name = "guide_details")
public class GuideDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(name = "guide_name")
    @NotBlank(message = "guideName must not be null")
    private String guideName;
    @Column(name = "phone_number")
    @NotBlank(message = "phoneNumber must not be null")
    private String phoneNumber;
    @Column(name = "specialisation")
    @NotNull(message = "specialisation must not be null")
    private Specialisation specialisation;
    @Column(name = "closest_big_city")
    @NotBlank(message = "closestBigCity must not be null")
    private String closestBigCity;
    @Column(name = "experience_level")
    @NotNull(message = "experienceLevel must not be null")
    private ExperienceLevel experienceLevel;
    @Column(name = "counter_of_old_journey")
    @Min(0)
    private int counterOfDidJourney;
    @JsonManagedReference
    @OneToMany(mappedBy = "guide_details", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GuideAnnouncementTable> guideAnnouncements;
    @JsonManagedReference
    @OneToMany(mappedBy = "guide_details", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Review>reviews;
    @OneToOne
    @JsonBackReference
    @JoinColumn(name = "user_id")
    private User user;
    @ManyToOne
    @JoinColumn(name = "region_id", nullable = false)
    private Region region;
}
