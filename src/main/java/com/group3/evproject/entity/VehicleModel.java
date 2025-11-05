package com.group3.evproject.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@Table(name = "vehicle_model")
public class VehicleModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne()
    @JoinColumn(name = "brand_id")
    @JsonBackReference
    VehicleBrand brand;

    @Column(name = "model_name")
    String modelName;

    String connector;

    @Column(name = "battery_capacity")
    Double batteryCapacity;

    @Column(name = "img_url")
    String url;

    @OneToMany(mappedBy = "model",cascade = CascadeType.ALL)
    @JsonManagedReference
    List<Vehicle> vehicles;
}
