package com.bobjool.payment.application.interfaces;

import com.bobjool.payment.domain.entity.Payment;

public interface PgClient {
    boolean requestPayment(Payment payment);
    boolean cancelPayment(Payment payment);
}
