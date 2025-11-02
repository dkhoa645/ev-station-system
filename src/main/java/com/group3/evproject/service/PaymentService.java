package com.group3.evproject.service;

import com.group3.evproject.Enum.PaymentStatus;
import com.group3.evproject.entity.Company;
import com.group3.evproject.entity.Payment;
import com.group3.evproject.entity.User;
import com.group3.evproject.repository.PaymentRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PaymentService {
    PaymentRepository paymentRepository;

    public Payment save(Payment payment){
        return paymentRepository.save(payment);
    }

    public Payment createNew(User user, Company company){

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime period = LocalDateTime.of(now.getYear(), now.getMonth(), 25, 0, 0);
        if (now.getDayOfMonth() > 25) {
            period = period.plusMonths(1);
        }
        Payment payment = Payment.builder()
                .user(user)
                .company(company)
                .totalEnergy(BigDecimal.ZERO)
                .totalCost(BigDecimal.ZERO)
                .status(PaymentStatus.UNPAID)
                .period(period)
                .invoices(new ArrayList<>())
                .paymentTransactions(new ArrayList<>())
                .build();
        return payment;
    }
}
