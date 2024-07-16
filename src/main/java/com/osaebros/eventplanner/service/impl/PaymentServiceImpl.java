package com.osaebros.eventplanner.service.impl;

import com.osaebros.eventplanner.entity.Booking;
import com.osaebros.eventplanner.repository.dto.BookingDto;
import com.osaebros.eventplanner.service.PaymentService;
import com.stripe.exception.StripeException;
import com.stripe.model.Product;
import com.stripe.model.checkout.Session;
import com.stripe.param.ProductCreateParams;
import com.stripe.param.checkout.SessionCreateParams;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.String.format;

@Slf4j
@Service
public class PaymentServiceImpl implements PaymentService {

    @Value("${application.frontend_url}")
    private String redirecturl;

    @Override
    public String getStripePaymentSession(Booking booking) {
        try {
            long start = System.currentTimeMillis();
            String bookingref = booking.getBookingId();
            log.info("Creating stripe session for booking: {}", bookingref);

            ProductCreateParams productCreateParams = createProductParameters(booking);

            Product product = Product.create(productCreateParams);
            log.info("Created price item {}", product.getId());

            SessionCreateParams paymentSessionParams = createPaymentSessionParams(product, booking.getBookingId());
            Session session = Session.create(paymentSessionParams);
            log.info("Payment session status {}. {}ms", session.getStatus(), System.currentTimeMillis() - start);
            return session.getClientSecret();

        } catch (StripeException e) {
            log.error("An Error with processing stripe payment: {}, {}", e.getMessage(), e.getStripeError().getMessage());
        }
        return null;
    }


    @Override
    public Map<String, String> viewSessionStatus(String checkoutSessionId) throws StripeException {
        Map<String, String> stripeResponse = new HashMap<>();
        Session session = Session.retrieve(checkoutSessionId);
        stripeResponse.put("status", session.getStatus());
        stripeResponse.put("paymentStatus", session.getPaymentStatus());
        stripeResponse.put("customerId", session.getCustomer());
        stripeResponse.put("bookingId", session.getClientReferenceId());
        return stripeResponse;
    }

    private ProductCreateParams createProductParameters(Booking booking) {
        String bookingref = booking.getBookingId();
        String serviceProviderName = booking.getProvider().getUsername();
        List<ProductCreateParams.Feature> features = createFeaturesList(booking);
        ProductCreateParams.DefaultPriceData defaultPriceData = createDefaultPriceData(booking);
        String statementDescriptor = format("Ref: %s", bookingref);

        return ProductCreateParams.builder()
                .setName(format("Booking confirmation deposit: %s", serviceProviderName))
                .setStatementDescriptor(statementDescriptor) //Maximum of 22 characters, refactor to enum template
                .addAllFeature(features)
                .setDefaultPriceData(defaultPriceData)
                .build();
    }

    private ProductCreateParams.DefaultPriceData createDefaultPriceData(Booking booking) {
        Long depositAmount = booking.getCost().longValue();

        return ProductCreateParams.
                DefaultPriceData.builder()
                .setCurrency("GBP")
                .setUnitAmount(depositAmount) //Refactor to be 10% of booking fee - must be larger than 0.3gbp (30L)
                .build();
    }

    private List<ProductCreateParams.Feature> createFeaturesList(Booking booking) {

        return List.of(ProductCreateParams.Feature.builder() //Add more details about event to item
                .setName("booking id:" + booking.getBookingId())
                .build()
        );
    }

    private SessionCreateParams createPaymentSessionParams(Product product, String bookingId) {
        SessionCreateParams.LineItem lineItem = SessionCreateParams.LineItem
                .builder()
                .setQuantity(1L)
                .setPrice(product.getDefaultPrice())
                .build();

        return SessionCreateParams.builder()
                .setClientReferenceId(bookingId)
                .setUiMode(SessionCreateParams.UiMode.EMBEDDED)
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setReturnUrl(redirecturl + "/bookingSummary?session_id={CHECKOUT_SESSION_ID}")
                .addLineItem(lineItem)
                .build();
    }
}
