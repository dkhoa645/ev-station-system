package com.group3.evproject.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "vehicle")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Vehicle {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne @JoinColumn(name = "user_id")
    @JsonIgnore
    User user;

    @Column(name = "license_plate")
    String licensePlate;

    @ManyToOne @JoinColumn(name = "model_id")
    @JsonBackReference
    VehicleModel model;

    @OneToOne(mappedBy = "vehicle", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    VehicleSubscription subscription;

    @OneToMany(mappedBy = "vehicle", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Booking> bookings;

}

