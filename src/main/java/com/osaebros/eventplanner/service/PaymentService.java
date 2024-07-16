package com.osaebros.eventplanner.service;

import com.osaebros.eventplanner.entity.Booking;
import com.stripe.exception.StripeException;

import java.util.Map;

public interface PaymentService {

    String getStripePaymentSession(Booking booking);

    Map<String, String> viewSessionStatus(String checkoutSessionId) throws StripeException;


}
