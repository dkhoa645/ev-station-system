package com.group3.evproject.entity;

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

    @Column(nullable = false)
    private String name;

    @Column(name = "power_output")
    private Double powerOutput;

    @Column(nullable = false)
    private String status = "Available";  // e.g. "AVAILABLE", "OCCUPIED", "MAINTENANCE"

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "station_id", nullable = false)
    private ChargingStation station;
}
