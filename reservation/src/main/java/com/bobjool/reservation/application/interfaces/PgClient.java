package com.bobjool.reservation.application.interfaces;

import com.bobjool.reservation.domain.entity.Payment;

public interface PgClient {
    boolean requestPayment(Payment payment);
    boolean cancelPayment(Payment payment);
}
