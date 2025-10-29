package com.group3.evproject.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "charging_points")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChargingPoint {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String stationId;
    private String type;
    private String status;
}
