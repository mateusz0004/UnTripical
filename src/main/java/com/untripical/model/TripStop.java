package com.untripical.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

@Table(name = "trip_stops")
@Entity
@Data
public class TripStop {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotBlank (message = "description must not be blank")
    @Column(name = "description")
    private String description;

    @Min(1)
    @Column(name = "order_index")
    @NotNull(message = "orderIndex must not be null")
    private int orderIndex;

    @Column(name = "distance_to_next")
    private Double distanceToNext;

    @ManyToOne
    @JsonBackReference
    @JoinColumn (name = "trip_plan_id")
    private TripPlan tripPlan;

    @ManyToOne
    @JsonBackReference
    @JoinColumn (name = "place_id")
    private Place place;
}
