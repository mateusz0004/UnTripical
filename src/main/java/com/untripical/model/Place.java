package com.untripical.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.untripical.enums.VerificationStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
@Table(name = "places")
@Entity
public class Place {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(name = "name")
    @NotBlank(message = "name must not be blank")
    private String name;
    @Column(name = "city")
    @NotBlank(message = "city must not be blank")
    private String city;
    @Column(name = "latitude")
    @NotNull(message = "latitude must not be null")
    private Double latitude;
    @Column(name = "longitude")
    @NotNull(message = "longitude must not be null")
    private Double longitude;
    @Column(name = "photo_url")
    @NotBlank(message = "photoUrl must not be blank")
    private String photoUrl;
    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    @NotNull(message = "status must not be null")
    private VerificationStatus status;
    @JsonManagedReference
    @OneToMany(mappedBy = "place", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TripStop> tripStops;
    @JsonManagedReference
    @OneToMany(mappedBy = "place", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Review> reviews;
    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "region_id")
    private Region region;
    @JsonManagedReference
    @OneToMany(mappedBy = "place", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Announcement> announcements;
    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
