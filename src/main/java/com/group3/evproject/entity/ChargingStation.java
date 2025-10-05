package com.group3.evproject.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "charging_station")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ChargingStation {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    String name;
    String location;
}
