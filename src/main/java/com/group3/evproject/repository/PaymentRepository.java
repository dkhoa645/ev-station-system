package com.group3.evproject.repository;

import com.group3.evproject.entity.Company;
import com.group3.evproject.entity.Payment;
import com.group3.evproject.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment,Long> {
    List<Payment> findByCompany(Company company);

    Payment findByCompanyAndPeriod(Company company, LocalDateTime finalPeriod);

    Payment findByUserAndPeriod(User user, LocalDateTime finalPeriod);
}
