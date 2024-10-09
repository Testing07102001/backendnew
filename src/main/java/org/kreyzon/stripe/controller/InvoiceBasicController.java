package org.kreyzon.stripe.controller;

import org.kreyzon.stripe.entity.InvoiceBasic;
import org.kreyzon.stripe.exception.VenueNotFoundException;
import org.kreyzon.stripe.repository.InvoiceBasicRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(origins = "https://impulz-lms.com")
public class InvoiceBasicController {
    @Autowired
    private InvoiceBasicRepository invoiceBasicRepository;

    @PostMapping("/invoicebasic")
    public ResponseEntity<InvoiceBasic> newInvoiceBasic(@RequestBody InvoiceBasic newInvoiceBasic) {
        InvoiceBasic invoiceBasic = invoiceBasicRepository.save(newInvoiceBasic);
        return ResponseEntity.status(HttpStatus.CREATED).body(invoiceBasic);
    }

    @GetMapping("/invoicebasic")
    public ResponseEntity<List<InvoiceBasic>> getAllInvoiceBasics() {
        List<InvoiceBasic> invoiceBasicList = invoiceBasicRepository.findAll();
        return ResponseEntity.ok(invoiceBasicList);
    }

    @GetMapping("/invoicebasic/{id}")
    public ResponseEntity<InvoiceBasic> getInvoiceBasicById(@PathVariable Long id) {
        InvoiceBasic invoiceBasic = invoiceBasicRepository.findById(id)
                .orElseThrow(() -> new VenueNotFoundException(id));
        return ResponseEntity.ok(invoiceBasic);
    }

    @PutMapping("/invoicebasic/{id}")
    public ResponseEntity<InvoiceBasic> updateInvoiceBasic(
            @RequestBody InvoiceBasic newInvoiceBasic, @PathVariable Long id) {
        InvoiceBasic invoiceBasic = invoiceBasicRepository.findById(id)
                .map(existingInvoiceBasic -> {
                    existingInvoiceBasic.setInvoiceNo(newInvoiceBasic.getInvoiceNo());
                    existingInvoiceBasic.setInvoiceStart(newInvoiceBasic.getInvoiceStart());
                    existingInvoiceBasic.setPayment(newInvoiceBasic.getPayment());


                    return invoiceBasicRepository.save(existingInvoiceBasic);
                })
                .orElseThrow(() -> new VenueNotFoundException(id));


        return ResponseEntity.ok(invoiceBasic);
    }
    @DeleteMapping("/invoicebasic/{id}")
    public ResponseEntity<String> deleteInvoiceBasic(@PathVariable Long id) {
        if (!invoiceBasicRepository.existsById(id)) {
            throw new VenueNotFoundException(id);
        }


        invoiceBasicRepository.deleteById(id);
        return ResponseEntity.ok("InvoiceBasic with ID " + id + " has been deleted successfully.");
    }

    @ControllerAdvice
    public class InvoiceBasicNotFoundAdvice {


        @ResponseBody
        @ExceptionHandler(VenueNotFoundException.class)
        @ResponseStatus(HttpStatus.NOT_FOUND)
        public Map<String, String> invoiceBasicExceptionHandler(VenueNotFoundException exception) {
            Map<String, String> errorMap = new HashMap<>();
            errorMap.put("errorMessage", exception.getClass().getName());
            return errorMap;
        }
    }
}
