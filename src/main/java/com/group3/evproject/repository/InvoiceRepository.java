package com.group3.evproject.repository;

import com.group3.evproject.entity.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface InvoiceRepository  extends JpaRepository<Invoice,Long> {

    List<Invoice> findBySession_Booking_User_Id(Long userId);
    Optional<Invoice> findBySession_Id (Long sessionId);
    List<Invoice> findBySession_Booking_Vehicle_Id (Long vehicleId);
    List<Invoice> findByStatus (Invoice.Status status);

}
