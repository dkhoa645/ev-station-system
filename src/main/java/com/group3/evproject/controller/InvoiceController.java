package com.group3.evproject.controller;

import com.group3.evproject.entity.Invoice;
import com.group3.evproject.service.InvoiceService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/invoices")
@RequiredArgsConstructor
@CrossOrigin("*")

public class InvoiceController {

    private final InvoiceService invoiceService;

    @GetMapping
    public ResponseEntity<List<Invoice>> getAllInvoices() {
        return ResponseEntity.ok(invoiceService.getAllInvoices());
    }
    @GetMapping("/session/{sessionId}")
    public ResponseEntity<Invoice> getInvoiceBySession(@PathVariable Long sessionId) {
        return ResponseEntity.ok(invoiceService.getInvoiceBySessionId(sessionId));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Invoice>> getInvoicesByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(invoiceService.getInvoicesByUserId(userId));
    }

    @GetMapping("/vehicle/{vehicleId}")
    public ResponseEntity<List<Invoice>> getInvoicesByVehicle(@PathVariable Long VehicleId) {
        return ResponseEntity.ok(invoiceService.getInvoicesByVehicle(VehicleId));
    }

    @GetMapping("/Status")
    public ResponseEntity<List<Invoice>> getInvoicesByStatus (String status) {
        return ResponseEntity.ok(invoiceService.getPedingInvoices(status));
    }

    @PostMapping
    public ResponseEntity<Invoice> createInvoice(@PathVariable Long id) {
        Invoice saved = invoiceService.createInvoice(id);
        return ResponseEntity.ok(saved);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteInvoice(@PathVariable Long id) {
        try {
            invoiceService.deleteInvoice(id);
            return ResponseEntity.ok("Invoice deleted successfully");
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }
}

