package site.billingwise.api.serverapi.domain.contract.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import site.billingwise.api.serverapi.domain.contract.Contract;

public interface ContractRepository extends JpaRepository<Contract, Long>, JpaSpecificationExecutor<Contract> {

    @Query("SELECT c FROM Contract c "
            + "JOIN FETCH c.item "
            + "JOIN FETCH c.member "
            + "WHERE c.id = :contractId")
    Optional<Contract> findWithItemWithMemberById(@Param("contractId") Long contractId);
}
