package com.group3.evproject.repository;

import com.group3.evproject.entity.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface InvoiceRepository  extends JpaRepository<Invoice,Long> {

    //lấy invoice theo userId
    @Query("SELECT i from Invoice i where i.payment.user.Id = :userId")
    List<Invoice> findByUserId(@Param("userId") Long userId);

    //lấy invoice theo sessionId
    Optional<Invoice> findBySession_Id (Long sessionId);

    //lấy invoice theo bookingId
    @Query("SELECT i FROM Invoice i WHERE i.session.booking.id = :bookingId")
    List<Invoice> findByBookingId(@Param("bookingId") Long bookingId);


}
