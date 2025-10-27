package com.group3.evproject.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "charging_spot")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class ChargingSpot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String spotName;

    @Column(name = "power_output")
    Double powerOutput;

    @Column(nullable = false)
    String status = "AVAILABLE";

    @Column(nullable = false)
    boolean available = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "station_id", nullable = false)
    @JsonBackReference
    ChargingStation station;

    @Enumerated(EnumType.STRING)
    @Column(name = "spot_type", nullable = false)
    SpotType spotType;

    public enum SpotType {
        WALK_IN,
        BOOKING
    }
}
