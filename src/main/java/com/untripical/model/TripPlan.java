package com.untripical.model;


import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Date;
import java.util.List;

@Table (name = "trip_plans")
@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
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

    @NotNull(message = "date must not be null")
    @Column (name = "date")
    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate dayWhenTripPlanIsStarting;

    @Column(name = "totalDistance")
    private Double totalDistance;

    @OneToMany(mappedBy = "tripPlan", cascade = CascadeType.ALL, orphanRemoval = true)
   // @JsonManagedReference
    private List<TripStop> tripStops;

    public void addTripStop(TripStop tripStop){
        this.tripStops.add(tripStop);
        tripStop.setTripPlan(this);
    }

    public void removeTripStop (TripStop tripStop){
        this.tripStops.remove(tripStop);
        tripStop.setTripPlan(null);
    }

    @ManyToOne
    @JsonBackReference
    @JoinColumn (name = "user_id")
    private User user;

    @PrePersist
    protected void onCreateAndActive() {
        this.isActive=true;
        this.assignedAt = new Date();
    }

}
