package com.bobjool.reservation.application.interfaces;

import com.bobjool.reservation.application.events.ReservationCompletedEvent;
import com.bobjool.reservation.application.events.ReservationCreatedEvent;
import com.bobjool.reservation.application.events.ReservationFailedEvent;

public interface ReservationProducer {
    void publishReservationCreated(String topic, ReservationCreatedEvent event);
    void publishReservationCompleted(String topic, ReservationCompletedEvent event);
    void publishReservationFailed(String topic, ReservationFailedEvent event);
}
