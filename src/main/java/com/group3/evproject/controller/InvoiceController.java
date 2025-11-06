package com.group3.evproject.controller;

import com.group3.evproject.entity.Invoice;
import com.group3.evproject.service.InvoiceService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
    public ResponseEntity<?> getInvoiceBySession(@PathVariable Long sessionId) {
        try {
            Invoice invoice = invoiceService.getInvoiceBySessionId(sessionId);
            return ResponseEntity.ok(invoice);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Invoice>> getInvoicesByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(invoiceService.getInvoicesByUserId(userId));
    }

    @GetMapping("/vehicle/{vehicleId}")
    public ResponseEntity<List<Invoice>> getInvoicesByVehicle(@PathVariable Long vehicleId) {
        return ResponseEntity.ok(invoiceService.getInvoicesByVehicle(vehicleId));
    }

    @GetMapping("/status")
    public ResponseEntity<List<Invoice>> getInvoicesByStatus(@PathVariable String status) {
        return ResponseEntity.ok(invoiceService.getPedingInvoices(status));
    }

    @PostMapping("/session/{sessionId}")
    public ResponseEntity<?> createInvoiceBySessionId(@PathVariable Long sessionId) {
        try {
            Invoice saved = invoiceService.createInvoice(sessionId);
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
        } catch (EntityNotFoundException | IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/{id}")
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
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}

