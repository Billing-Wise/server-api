package site.billingwise.api.serverapi.domain.payment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import site.billingwise.api.serverapi.domain.payment.PaymentAccount;
import site.billingwise.api.serverapi.domain.payment.PaymentCard;

public interface PaymentAccountRepository  extends JpaRepository<PaymentAccount, Long> {
}
