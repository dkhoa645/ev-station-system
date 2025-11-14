package com.group3.evproject.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "user")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)  // tất cả field đều là private
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @Column(unique = true, nullable = false)
    String email;
    @Column(unique = true, nullable = false)
    String username;
    String password;
    String name;
    @Column(name = "verification_token")
    String verificationToken;
    @Column(name = "verified")
    boolean verified = false;

    @Column(name = "reset_token")
    String resetToken;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_role",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    Set<Role> roles = new HashSet<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    List<Vehicle> vehicles;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JsonIgnore
    List<Booking> bookings;

    @ManyToOne()
    @JoinColumn(name = "company_id",nullable = true)
    Company company;

    @OneToMany(mappedBy = "user",cascade = CascadeType.ALL)
    List<Payment> payments;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    List<PaymentTransaction> paymentTransactionList;
}