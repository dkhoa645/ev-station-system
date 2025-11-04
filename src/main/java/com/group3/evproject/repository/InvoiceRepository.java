package com.group3.evproject.repository;

import com.group3.evproject.entity.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface InvoiceRepository  extends JpaRepository<Invoice,Long> {

//    @Query("SELECT i from Invoice i where i.payment.userId =: userId")
//    List<Invoice> findInvoiceByUserId(@Param("userId") Long userId);

}
