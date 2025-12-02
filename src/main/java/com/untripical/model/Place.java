package com.untripical.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.untripical.enums.PlaceType;
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

    @NotBlank(message = "address street must not be blank")
    @Column(name = "address_street")
    private String addressStreet;

    @NotBlank(message = "address must not be blank")
    @Column(name = "address_number")
    private String addressNumber;

    @NotNull(message = "placeType must not be null")
    @Column (name = "place_type")
    private PlaceType placeType;

    @NotNull(message = "postalCode must not be null")
    @Column (name = "postal_code")
    private String postalCode;

    @Column(name = "is_active")
    @NotNull(message = "isActive must not be null")
    private Boolean isActive;


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
    @OrderColumn(name = "order_index")
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

    @PrePersist
    protected void onCreate() {
        this.isActive = true;
    }
}
