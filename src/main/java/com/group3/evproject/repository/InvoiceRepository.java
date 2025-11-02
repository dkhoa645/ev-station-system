package com.group3.evproject.repository;

import com.group3.evproject.entity.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface InvoiceRepository  extends JpaRepository<Invoice,Long> {

    List<Invoice> findBySession_Id(Long sessionId);
    List<Invoice> findByPayment_Id(Long paymentId);
    List<Invoice> findByIssueDateBetween(LocalDateTime startDate, LocalDateTime endDate);

    @Query("SELECT SUM(i.finalCost) FROM Invoice i WHERE i.payment.id = :paymentId")
    Double getTotalCostByPayment(@Param("paymentId") Long paymentId);

    @Query("SELECT SUM(i.finalCost) FROM Invoice i WHERE i.issueDate BETWEEN :startDate AND :endDate")
    Double getTotalRevenueInPeriod(@Param("startDate") LocalDateTime startDate,
                                   @Param("endDate") LocalDateTime endDate);

    @Query("SELECT i FROM Invoice i WHERE FUNCTION('MONTH', i.issueDate) = :month AND FUNCTION('YEAR', i.issueDate) = :year")
    List<Invoice> findInvoicesByMonthAndYear(@Param("month") int month, @Param("year") int year);
}
