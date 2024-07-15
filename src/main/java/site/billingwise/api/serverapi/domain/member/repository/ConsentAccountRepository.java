package site.billingwise.api.serverapi.domain.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import site.billingwise.api.serverapi.domain.member.ConsentAccount;

public interface ConsentAccountRepository extends JpaRepository<ConsentAccount, Long>{
    
}
