package site.billingwise.api.serverapi.domain.payment.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import site.billingwise.api.serverapi.domain.payment.Payment;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    
}
