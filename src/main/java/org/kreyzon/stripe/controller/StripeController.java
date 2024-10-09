package org.kreyzon.stripe.controller;

import lombok.RequiredArgsConstructor;
import org.kreyzon.stripe.dto.CapturePaymentResponse;
import org.kreyzon.stripe.dto.CreatePaymentRequest;
import org.kreyzon.stripe.dto.SessionDto;
import org.kreyzon.stripe.dto.StripeResponse;
import org.kreyzon.stripe.service.StripeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/stripe")
@RequiredArgsConstructor
public class StripeController {

    private final StripeService stripeService;

    @PostMapping("/create-payment")
    public ResponseEntity<StripeResponse> createPayment(@RequestBody CreatePaymentRequest createPaymentRequest) {
        StripeResponse stripeResponse = stripeService.createPayment(createPaymentRequest);
        return ResponseEntity
                .status(stripeResponse.getHttpStatus())
                .body(stripeResponse);
    }

    @GetMapping("/capture-payment")
    public ResponseEntity<StripeResponse> capturePayment(@RequestParam String sessionId,
                                                         @RequestParam(required = false) String sessionStatus,
                                                         @RequestParam(required = false) String paymentStatus,
                                                         @RequestParam(required = false) String name,
                                                         @RequestParam(required = false) String client_reference_id,
                                                         @RequestParam(required = false) String amount_received) {
        CapturePaymentResponse capturePaymentResponse = CapturePaymentResponse.builder()
                .sessionId(sessionId)
                .sessionStatus(sessionStatus)
                .paymentStatus(paymentStatus)
                .name(name)
                .client_reference_id(client_reference_id)
                .amount_received(amount_received)
                .build();

        StripeResponse stripeResponse = stripeService.capturePayment(sessionId, capturePaymentResponse);
        return ResponseEntity
                .status(stripeResponse.getHttpStatus())
                .body(stripeResponse);
    }
    @GetMapping("/list-sessions")
    public ResponseEntity<List<SessionDto>> listAllSessions() {
        List<SessionDto> allSessions = stripeService.listAllSessions();
        return ResponseEntity.ok(allSessions);
    }

    @GetMapping("/paid-sessions")
    public ResponseEntity<List<SessionDto>> getAllPaidSessions() {
        List<SessionDto> paidSessions = stripeService.getAllPaidSessions();
        return ResponseEntity.ok(paidSessions);
    }

    @GetMapping("/unpaid-sessions")
    public ResponseEntity<List<SessionDto>> getAllUnpaidSessions() {
        List<SessionDto> unpaidSessions = stripeService.getAllUnpaidSessions();
        return ResponseEntity.ok(unpaidSessions);
    }
    @GetMapping("/sessions-by-client-reference-id")
    public ResponseEntity<List<SessionDto>> getSessionsByClientReferenceId(@RequestParam String clientReferenceId) {
        List<SessionDto> sessions = stripeService.getSessionsByClientReferenceId(clientReferenceId);
        return ResponseEntity.ok(sessions);
    }

}
