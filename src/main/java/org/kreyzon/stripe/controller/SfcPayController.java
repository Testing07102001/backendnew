package org.kreyzon.stripe.controller;

import org.kreyzon.stripe.entity.SfcPay;
import org.kreyzon.stripe.exception.VenueNotFoundException;
import org.kreyzon.stripe.repository.SfcPayRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "https://impulz-lms.com")
public class SfcPayController {
    @Autowired
    private SfcPayRepository sfcPayRepository;


    @PostMapping("/sfcpay")
    public ResponseEntity<SfcPay> newSfcPay(@RequestBody SfcPay newSfcPay) {
        SfcPay sfcPay = sfcPayRepository.save(newSfcPay);
        return ResponseEntity.status(HttpStatus.CREATED).body(sfcPay);
    }


    @GetMapping("/sfcpays")
    public ResponseEntity<List<SfcPay>> getAllSfcPays() {
        List<SfcPay> sfcPays = sfcPayRepository.findAll();
        return ResponseEntity.ok(sfcPays);
    }


    @GetMapping("/sfcpay/{id}")
    public ResponseEntity<SfcPay> getSfcPayById(@PathVariable Long id) {
        SfcPay sfcPay = sfcPayRepository.findById(id)
                .orElseThrow(() -> new VenueNotFoundException(id));
        return ResponseEntity.ok(sfcPay);
    }


    @PutMapping("/sfcpay/{id}")
    public ResponseEntity<SfcPay> updateSfcPay(@RequestBody SfcPay newSfcPay, @PathVariable Long id) {
        SfcPay sfcPay = sfcPayRepository.findById(id)
                .map(existingSfcPay -> {
                    existingSfcPay.setBatch(newSfcPay.getBatch());
                    existingSfcPay.setStudent(newSfcPay.getStudent());
                    existingSfcPay.setFee(newSfcPay.getFee());
                    existingSfcPay.setDocument(newSfcPay.getDocument());
                    return sfcPayRepository.save(existingSfcPay);
                })
                .orElseThrow(() -> new VenueNotFoundException(id));


        return ResponseEntity.ok(sfcPay);
    }


    @DeleteMapping("/sfcpay/{id}")
    public ResponseEntity<String> deleteSfcPay(@PathVariable Long id) {
        if (!sfcPayRepository.existsById(id)) {
            throw new VenueNotFoundException(id);
        }


        sfcPayRepository.deleteById(id);
        return ResponseEntity.ok("SfcPay with ID " + id + " has been deleted successfully.");
    }
}
