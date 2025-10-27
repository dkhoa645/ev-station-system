package com.group3.evproject.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "payment")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Payment {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    LocalDateTime startDate;
    LocalDateTime endDate;
    @ManyToOne
    @JoinColumn(name = "vehicle_id", nullable = false)
    private Vehicle vehicle;
    BigDecimal totalEnergy;
    BigDecimal totalCost;
    @Enumerated(EnumType.STRING)
    PaymentStatusEnum status;
    LocalDateTime createdAt;
    LocalDateTime paidAt;
    @OneToMany(mappedBy = "payment", cascade = CascadeType.ALL)
    List<Invoice> invoices ;

    @OneToMany(mappedBy = "payment", cascade = CascadeType.ALL)
    List<PaymentTransaction> paymentTransactions;

}
