package com.group3.evproject.service;

import com.group3.evproject.entity.Invoice;
import com.group3.evproject.repository.InvoiceRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InvoiceService {

    private final InvoiceRepository invoiceRepository;

    // Lấy tất cả hóa đơn
    public List<Invoice> getAllInvoices() {
        return invoiceRepository.findAll();
    }

    // Lấy hóa đơn theo ID
    public Invoice getInvoiceById(Long id) {
        return invoiceRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Invoice not found with id: " + id));
    }

    // Tạo mới hóa đơn
    public Invoice createInvoice(Invoice invoice) {
        return invoiceRepository.save(invoice);
    }

    // Cập nhật hóa đơn
    public Invoice updateInvoice(Long id, Invoice newInvoice) {
        Invoice existing = getInvoiceById(id);
        existing.setFinalCost(newInvoice.getFinalCost());
        existing.setIssueDate(newInvoice.getIssueDate());
        existing.setSession(newInvoice.getSession());
        existing.setPayment(newInvoice.getPayment());
        return invoiceRepository.save(existing);
    }

    // Xóa hóa đơn
    public void deleteInvoice(Long id) {
        if (!invoiceRepository.existsById(id)) {
            throw new EntityNotFoundException("Invoice not found with id: " + id);
        }
        invoiceRepository.deleteById(id);
    }
}
