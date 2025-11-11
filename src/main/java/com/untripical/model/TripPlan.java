package com.untripical.model;


import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Table (name = "trip_plans")
@Entity
@Data
public class TripPlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotBlank(message = "name must not be blank")
    @Column(name = "name")
    private String name;

    @NotNull(message = "assignedAt must not be null")
    @Column(name = "assigned_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date assignedAt;

    @NotNull (message = "isActive must not be null")
    @Column (name = "is_active")
    private Boolean isActive;

    @OneToMany (mappedBy = "tripPlan", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<TripStop> tripStops;

    @ManyToOne
    @JsonBackReference
    @JoinColumn (name = "user_id")
    private User user;

    @PrePersist
    protected void onCreate() {
        this.assignedAt = new Date();
    }
}
