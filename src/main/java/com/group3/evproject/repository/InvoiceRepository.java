package com.group3.evproject.repository;

import com.group3.evproject.entity.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface InvoiceRepository  extends JpaRepository<Invoice,Long> {

    List<Invoice> findBySession_Id(Long sessionId);
    List<Invoice> findByPayment_Id(Long paymentId);

}
