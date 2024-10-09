package org.kreyzon.stripe.service;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import lombok.extern.slf4j.Slf4j;
import org.kreyzon.stripe.dto.CapturePaymentResponse;
import org.kreyzon.stripe.dto.CreatePaymentRequest;
import org.kreyzon.stripe.dto.CreatePaymentResponse;
import org.kreyzon.stripe.dto.SessionDto;
import org.kreyzon.stripe.dto.StripeResponse;
import org.kreyzon.stripe.utils.Constant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.stripe.model.Event;
import com.stripe.model.StripeObject;
import com.stripe.model.checkout.Session;
import com.stripe.model.checkout.SessionCollection;
import com.stripe.param.checkout.SessionCreateParams;
import com.stripe.param.checkout.SessionListParams;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
public class StripeService {

    @PersistenceContext
    private EntityManager entityManager;
    private final EmailService emailService;

    @Autowired
    public StripeService(EmailService emailService) {
        this.emailService = emailService;
    }

    @Value("${stripe.secretKey}")
    private String secretKey;

    public StripeResponse createPayment(CreatePaymentRequest createPaymentRequest) {
        Stripe.apiKey = secretKey;

        SessionCreateParams.LineItem.PriceData.ProductData productData =
                SessionCreateParams.LineItem.PriceData.ProductData.builder()
                        .setName(createPaymentRequest.getName())
                        .build();


        SessionCreateParams.LineItem.PriceData priceData =
                SessionCreateParams.LineItem.PriceData.builder()
                        .setCurrency(createPaymentRequest.getCurrency())
                        .setUnitAmount(createPaymentRequest.getAmount())
                        .setProductData(productData)
                        .build();

        SessionCreateParams.LineItem lineItem =
                SessionCreateParams.LineItem.builder()
                        .setQuantity(createPaymentRequest.getQuantity())
                        .setPriceData(priceData)
                        .build();

        SessionCreateParams params =
                SessionCreateParams.builder()
                        .setMode(SessionCreateParams.Mode.PAYMENT)
                        .setSuccessUrl(createPaymentRequest.getSuccessUrl())
                        .setCancelUrl(createPaymentRequest.getCancelUrl())
                        .addLineItem(lineItem)
                        .putMetadata("name", createPaymentRequest.getName())
                        .putMetadata("client_reference_id", createPaymentRequest.getClient_reference_id())
                        .build();

        try {
            Session session = Session.create(params);
            CreatePaymentResponse responseData = CreatePaymentResponse.builder()
                    .sessionId(session.getId())
                    .sessionUrl(session.getUrl())
                    .name(createPaymentRequest.getName())
                    .client_reference_id(createPaymentRequest.getClient_reference_id())
                    .build();

            return StripeResponse.builder()
                    .status(Constant.SUCCESS)
                    .message("Payment session created successfully")
                    .httpStatus(200)
                    .data(responseData)
                    .build();

        } catch (StripeException e) {
            e.printStackTrace();
            return StripeResponse.builder()
                    .status(Constant.FAILURE)
                    .message("Payment session creation failed")
                    .httpStatus(400)
                    .data(null)
                    .build();
        }


    }

    public StripeResponse capturePayment(String sessionId, CapturePaymentResponse capturePaymentResponse) {
        Stripe.apiKey = secretKey;
        try {
            Session session = Session.retrieve(sessionId);
            String status = session.getStatus();

            if (status.equalsIgnoreCase(Constant.STRIPE_SESSION_STATUS_SUCCESS)) {
                log.info("Payment successfully captured.");
            }

            String name = capturePaymentResponse.getName() != null ? capturePaymentResponse.getName() : session.getMetadata().get("name");
            String clientReferenceId = capturePaymentResponse.getClient_reference_id() != null ? capturePaymentResponse.getClient_reference_id() : session.getMetadata().get("client_reference_id");
            String amountReceived = session.getAmountTotal() != null ? session.getAmountTotal().toString() : null;

            CapturePaymentResponse responseData = CapturePaymentResponse.builder()
                    .sessionId(sessionId)
                    .sessionStatus(status)
                    .paymentStatus(session.getPaymentStatus())
                    .name(name)
                    .client_reference_id(clientReferenceId)
                    .amount_received(amountReceived)
                    .build();

            return StripeResponse.builder()
                    .status(Constant.SUCCESS)
                    .message("Payment successfully captured.")
                    .httpStatus(200)
                    .data(responseData)
                    .build();

        } catch (StripeException e) {
            e.printStackTrace();
            return StripeResponse.builder()
                    .status(Constant.FAILURE)
                    .message("Payment capture failed due to a server error.")
                    .httpStatus(500)
                    .data(null)
                    .build();
        }
    }

    private SessionDto mapToSessionDto(Session session) {
        return SessionDto.builder()
                .sessionId(session.getId())
                .paid(session.getPaymentStatus())
                .amount(session.getAmountTotal())
                .name(session.getMetadata().get("name"))
                .client_reference_id(session.getMetadata().get("client_reference_id"))
                .build();
    }

    public List<SessionDto> listAllSessions() {
        Stripe.apiKey = secretKey;

        List<SessionDto> allSessions = new ArrayList<>();
        String startingAfter = null;

        try {
            do {
                SessionListParams params = SessionListParams.builder()
                        .setStartingAfter(startingAfter)
                        .build();

                SessionCollection sessions = Session.list(params);

                for (Session session : sessions.getData()) {
                    allSessions.add(mapToSessionDto(session));
                }

                startingAfter = sessions.getData().isEmpty() ? null : sessions.getData().get(sessions.getData().size() - 1).getId();
            } while (startingAfter != null);

            return allSessions;

        } catch (StripeException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    public void handleEvent(Event event) {
        StripeObject stripeObject = event.getDataObjectDeserializer().getObject().orElse(null);

        if (stripeObject == null) {
            log.error("Failed to deserialize event data object");
            return;
        }

        switch (event.getType()) {
            case "checkout.session.completed":
                Session session = (Session) stripeObject;
                updatePaymentStatus(session);
                break;
            default:
                log.warn("Unhandled event type: {}", event.getType());
                break;
        }
    }

    private void updatePaymentStatus(Session session) {
        String sessionId = session.getId();
        String paymentStatus = session.getPaymentStatus();
        log.info("Updating payment status for session ID: {} to status: {}", sessionId, paymentStatus);
    }

    public List<SessionDto> getAllPaidSessions() {
        return getSessionsByPaymentStatus(true);
    }

    public List<SessionDto> getAllUnpaidSessions() {
        return getSessionsByPaymentStatus(false);
    }

    private List<SessionDto> getSessionsByPaymentStatus(boolean paid) {
        Stripe.apiKey = secretKey;

        List<SessionDto> sessions = new ArrayList<>();
        String startingAfter = null;

        try {
            do {
                SessionListParams params = SessionListParams.builder()
                        .setStartingAfter(startingAfter)
                        .build();

                SessionCollection sessionCollection = Session.list(params);

                for (Session session : sessionCollection.getData()) {
                    if (session.getPaymentStatus().equals(paid ? "paid" : "unpaid")) {
                        sessions.add(mapToSessionDto(session));
                    }
                }

                startingAfter = sessionCollection.getData().isEmpty() ? null : sessionCollection.getData().get(sessionCollection.getData().size() - 1).getId();
            } while (startingAfter != null);
        } catch (StripeException e) {
            e.printStackTrace();
        }

        return sessions;
    }


    public List<SessionDto> getSessionsByClientReferenceId(String clientReferenceId) {
        Stripe.apiKey = secretKey;

        List<SessionDto> sessions = new ArrayList<>();
        String startingAfter = null;

        try {
            do {
                SessionListParams params = SessionListParams.builder()
                        .setStartingAfter(startingAfter)
                        .build();

                SessionCollection sessionCollection = Session.list(params);

                for (Session session : sessionCollection.getData()) {
                    if (Objects.equals(session.getMetadata().get("client_reference_id"), clientReferenceId)) {
                        sessions.add(mapToSessionDto(session));
                    }
                }

                startingAfter = sessionCollection.getData().isEmpty() ? null : sessionCollection.getData().get(sessionCollection.getData().size() - 1).getId();
            } while (startingAfter != null);
        } catch (StripeException e) {
            e.printStackTrace();
        }

        return sessions;
    }


}
