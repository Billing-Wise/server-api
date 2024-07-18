package site.billingwise.api.serverapi.domain.consent.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import site.billingwise.api.serverapi.domain.consent.ConsentAccount;
import site.billingwise.api.serverapi.domain.item.Item;

public interface ConsentAccountRepository extends JpaRepository<ConsentAccount, Long> {
}
