package com.untripical.untripical.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.untripical.untripical.enums.UserRole;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Date;

import java.util.List;

@Data
@Table(name = "users")
@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank(message = "email must not be blank")
    @Column(name = "email")
    private String email;
    @NotBlank(message = "username must not be blank")
    @Column(name = "username")
    private String username;
    @NotBlank(message = "password must not be blank")
    @Column(name = "password")
    private String password;
    @NotNull(message = "isActive must not be null")
    @Column(name = "is_active")
    private Boolean isActive;
    @NotNull(message = "createdAt must not be null")
    @Column(name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;
    @Enumerated(EnumType.STRING)
    @NotNull(message = "userRole must not be null")
    @Column(name="user_role")
    private UserRole userRole;
    @JsonManagedReference
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Place>places;
    @JsonManagedReference
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Review>reviews;
    @JsonManagedReference
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TripPlan>tripPlans;
    @PrePersist
    protected void onCreate() {
        this.createdAt = new Date();
    }
}
