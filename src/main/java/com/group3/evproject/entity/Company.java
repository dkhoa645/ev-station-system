package com.group3.evproject.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Entity
@Table(name = "company")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Company {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    String name;
    String address;
    String contactEmail;
    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL)
    List<User> user;
    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL)
    List<Payment> payment;
    @OneToMany(mappedBy = "company",cascade = CascadeType.ALL)
    List<Vehicle> vehicles;
}
