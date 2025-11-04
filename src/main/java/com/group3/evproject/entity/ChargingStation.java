package com.group3.evproject.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Entity
@Table(name = "charging_station")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema
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

    @Column(name = "image_url")
    String imageUrl;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    Integer totalSpotsOffline = 0;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    Integer totalSpotsOnline = 0;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    Integer totalSpots = 0;

    Double powerCapacity;
    Double latitude;
    Double longitude;

    @Column(name = "price_per_kwh")
    Double pricePerKwh;

    @OneToMany(mappedBy = "station", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference
    @JsonIgnore
    List<ChargingSpot> spots;
}
