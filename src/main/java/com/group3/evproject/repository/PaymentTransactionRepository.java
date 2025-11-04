package com.group3.evproject.repository;

import com.group3.evproject.entity.PaymentTransaction;
import com.group3.evproject.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentTransactionRepository extends JpaRepository<PaymentTransaction,Long> {
    Optional<PaymentTransaction> findByVnpTxnRef(String vnpTxnRef);

    List<PaymentTransaction> findByUser(User user);
}
