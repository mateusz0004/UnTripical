package com.untripical.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.untripical.enums.RegionType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
@Table(name = "region")
@Entity
public class Region {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull(message = "regionType must not be null")
    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private RegionType type;

    @NotNull(message = "isActive must not be null")
    private Boolean isActive;

    @NotBlank(message = "closestBigCity must not be blank")
    @Column(name = "closest_big_city")
    private String closestBigCity;

    @OneToMany(mappedBy = "region", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Place> places;

    @OneToMany(mappedBy = "region", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<GuideDetails> guides;

    @PrePersist
    protected void onCreate() {
         this.isActive = true;
    }
}
