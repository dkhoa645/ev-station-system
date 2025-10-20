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

    private String name;

    @Column(name = "power_output")
    private Double powerOutput;

    private String status;  // e.g. "AVAILABLE", "OCCUPIED", "MAINTENANCE"

    private boolean available = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "station_id")
    private ChargingStation station;
}
