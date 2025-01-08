package com.bobjool.payment.application.interfaces;

import com.bobjool.payment.application.events.PaymentCompletedEvent;
import com.bobjool.payment.application.events.PaymentFailedEvent;

public interface PaymentProducer {
    void send(String topic, PaymentCompletedEvent event);
    void send(String topic, PaymentFailedEvent event);
}
