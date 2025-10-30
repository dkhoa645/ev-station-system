package com.group3.evproject.entity;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "charging_spot")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class ChargingSpot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(nullable = false, unique = true)
    String spotName;

    Double powerOutput;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    SpotStatus status = SpotStatus.AVAILABLE;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "station_id", nullable = false)
    ChargingStation station;

    Boolean available = true;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    SpotType spotType;

    @OneToMany(mappedBy = "spot", cascade = CascadeType.ALL)
    List<Booking> bookings;

    public enum SpotType { WALK_IN, BOOKING }
    public enum SpotStatus { AVAILABLE, OCCUPIED, MAINTENANCE }
}
