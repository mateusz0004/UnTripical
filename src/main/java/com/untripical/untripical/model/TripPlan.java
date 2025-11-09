package com.untripical.untripical.model;


import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

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


    @NotNull(message = "assignedAt must not be null")
    @Column(name = "assigned_at")
    private Date assignedAt;

    @NotNull (message = "isActive must not be null")
    @Column (name = "is_active")
    private boolean isActive;

    @OneToMany (mappedBy = "tripPlan", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    @JoinColumn (name = "trip_stop_id")
    private List<TripStop> tripStops;

    @ManyToOne
    @JsonBackReference
    @JoinColumn (name = "user_id")
    private User user;
}
