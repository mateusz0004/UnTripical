package com.untripical.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Date;

@Table(name = "reviews")
@Entity
@Data
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Min(1)
    @Max(5)
    @NotNull (message = "numberOfStars must not be null")
    @Column (name = "number_of_stars")
    private Double numberOfStars;

    @NotBlank(message = "description must not be blank")
    @Column(name = "description")
    private String description;

    @NotNull(message = "createdAt must not be null")
    @Column(name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @ManyToOne
    @JsonBackReference
    @JoinColumn (name = "place_id")
    private Place place;

    @ManyToOne
    @JsonBackReference
    @JoinColumn (name = "user_id")
    private User user;

    @ManyToOne
    @JsonBackReference
    @JoinColumn (name = "guide_details_id")
    private GuideDetails guideDetails;

    @PrePersist
    protected void onCreate() {
        this.createdAt = new Date();
    }
}
