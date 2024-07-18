package site.billingwise.api.serverapi.domain.payment.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import feign.Param;
import site.billingwise.api.serverapi.domain.payment.Payment;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    @Query("SELECT p FROM Payment p "
            + "JOIN FETCH p.invoice i "
            + "JOIN FETCH i.contract c "
            + "JOIN FETCH c.member m "
            + "JOIN FETCH c.item "
            + "JOIN FETCH m.client "
            + "WHERE p.id = :invoiceId")
    Optional<Payment> findById(@Param("invoiceId") Long invoiceId);
}
