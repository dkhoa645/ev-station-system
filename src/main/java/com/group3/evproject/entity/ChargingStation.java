package com.group3.evproject.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Entity
@Table(name = "charging_station")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ChargingStation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    String name;

    @Column(nullable = false)
    String location;

    @Column(nullable = false)
    String status = "AVAILABLE";

    @Column(nullable = false)
    Integer bookingAvailable = 0;

    Double powerCapacity;
    Integer availableSpots;
    Double latitude;
    Double longitude;

    @OneToMany(mappedBy = "station", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference
    List<ChargingSpot> spots;
}
