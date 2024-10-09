package org.kreyzon.stripe.controller;

import org.kreyzon.stripe.entity.InvoiceSettings;
import org.kreyzon.stripe.exception.VenueNotFoundException;
import org.kreyzon.stripe.repository.InvoiceSettingsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "https://impulz-lms.com")
public class InvoiceSettingsController {
    @Autowired
    private InvoiceSettingsRepository invoiceSettingsRepository;


    @PostMapping("/invoicesettings")
    public ResponseEntity<InvoiceSettings> newInvoiceSettings(@RequestBody InvoiceSettings newInvoiceSettings) {
        InvoiceSettings invoiceSettings = invoiceSettingsRepository.save(newInvoiceSettings);
        return ResponseEntity.status(HttpStatus.CREATED).body(invoiceSettings);
    }

    @GetMapping("/invoicesettings")
    public ResponseEntity<List<InvoiceSettings>> getAllInvoiceSettings() {
        List<InvoiceSettings> invoiceSettingsList = invoiceSettingsRepository.findAll();
        return ResponseEntity.ok(invoiceSettingsList);
    }


    @GetMapping("/invoicesettings/{id}")
    public ResponseEntity<InvoiceSettings> getInvoiceSettingsById(@PathVariable Long id) {
        InvoiceSettings invoiceSettings = invoiceSettingsRepository.findById(id)
                .orElseThrow(() -> new VenueNotFoundException(id));
        return ResponseEntity.ok(invoiceSettings);
    }

    @PutMapping("/invoicesettings/{id}")
    public ResponseEntity<InvoiceSettings> updateInvoiceSettings(
            @RequestBody InvoiceSettings newInvoiceSettings, @PathVariable Long id) {
        InvoiceSettings invoiceSettings = invoiceSettingsRepository.findById(id)
                .map(existingInvoiceSettings -> {
                    existingInvoiceSettings.setName(newInvoiceSettings.getName());
                    existingInvoiceSettings.setType(newInvoiceSettings.getType());
                    existingInvoiceSettings.setCalculator(newInvoiceSettings.getCalculator());
                    existingInvoiceSettings.setValue(newInvoiceSettings.getValue());
                    return invoiceSettingsRepository.save(existingInvoiceSettings);
                })
                .orElseThrow(() -> new VenueNotFoundException(id));


        return ResponseEntity.ok(invoiceSettings);
    }


    @DeleteMapping("/invoicesettings/{id}")
    public ResponseEntity<String> deleteInvoiceSettings(@PathVariable Long id) {
        if (!invoiceSettingsRepository.existsById(id)) {
            throw new VenueNotFoundException(id);
        }


        invoiceSettingsRepository.deleteById(id);
        return ResponseEntity.ok("InvoiceSettings with ID " + id + " has been deleted successfully.");
    }
}
