package site.billingwise.api.serverapi.domain.contract.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import site.billingwise.api.serverapi.domain.contract.Contract;
import site.billingwise.api.serverapi.domain.contract.ContractStatus;
import site.billingwise.api.serverapi.domain.contract.PaymentType;
import site.billingwise.api.serverapi.domain.member.Member;

public interface ContractRepository extends JpaRepository<Contract, Long>, JpaSpecificationExecutor<Contract> {

    @Query("SELECT c FROM Contract c "
            + "JOIN FETCH c.item "
            + "JOIN FETCH c.member "
            + "WHERE c.id = :contractId")
    Optional<Contract> findWithItemWithMemberById(@Param("contractId") Long contractId);

    @Query("SELECT c FROM Contract c "
            + "JOIN FETCH c.member "
            + "WHERE c.id = :contractId")
    Optional<Contract> findWithMemberById(@Param("contractId") Long contractId);

    List<Contract> findAllByMemberAndPaymentTypeAndContractStatusAndIsEasyConsent(
            Member member,
            PaymentType paymentType,
            ContractStatus contractStatus,
            Boolean isEasyConsent);

    List<Contract> findAllByMemberAndPaymentTypeAndContractStatus(
            Member member,
            PaymentType paymentType,
            ContractStatus contractStatus);
}
