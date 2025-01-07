package com.bobjool.payment.infra.repository;

import com.bobjool.payment.domain.entity.Payment;
import com.bobjool.payment.domain.repository.PaymentRepository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

/**
 * 해당 인터페이스를 상속받는 클래스를 스프링이 자동으로 만들어주고
 * domain 에 PaymentRepository 에 주입해줍니다.
 * */
public interface PaymentRepositoryImpl
        extends JpaRepository<Payment, UUID>, PaymentRepository, PaymentRepositoryCustom {
}
