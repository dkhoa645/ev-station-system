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
    private Double powerOutput;

    //Enum trạng thái thực tế của điểm sạc
    @Enumerated(EnumType.STRING)
    @Column(name = "spot_status", nullable = false)
    private SpotStatus status = SpotStatus.AVAILABLE;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "station_id", nullable = false)
    @JsonBackReference
    private ChargingStation station;

    @Column(nullable = false)
    @Builder.Default
    private Boolean available = true;

    @Enumerated(EnumType.STRING)
    @Column(name = "spot_type", nullable = false)
    private SpotType spotType;

    public enum SpotType {
        WALK_IN,
        BOOKING
    }

    public enum SpotStatus {
        AVAILABLE,
        UNAVAILABLE,
        OCCUPIED
    }

}
