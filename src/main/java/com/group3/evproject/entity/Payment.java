package com.group3.evproject.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.group3.evproject.Enum.PaymentStatus;
import com.group3.evproject.entity.Company;
import com.group3.evproject.entity.Invoice;
import com.group3.evproject.entity.PaymentTransaction;
import com.group3.evproject.entity.User;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "payment")
@Getter
@Setter
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    BigDecimal totalEnergy;
    BigDecimal totalCost;
    BigDecimal paidCost;

    @Enumerated(EnumType.STRING)
    PaymentStatus status;
    LocalDateTime paidAt;
    LocalDateTime period;

    //orphanRemoval = true: Nếu xóa invoice khỏi list thì nó tự xóa DB
    //Khởi tạo new ArrayList<>() tránh NullPointerException
    @OneToMany(mappedBy = "payment", orphanRemoval = true)
    @JsonManagedReference
    List<Invoice> invoices = new ArrayList<>();

    @OneToMany(mappedBy = "payment", cascade = CascadeType.ALL)
    List<PaymentTransaction> paymentTransactions;

    @ManyToOne()
    @JoinColumn(name = "user_id", nullable = true)
    User user;

    @ManyToOne()
    @JoinColumn(name = "company_id", nullable = true)
    Company company;
}


