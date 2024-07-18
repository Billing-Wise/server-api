package site.billingwise.api.serverapi.domain.invoice.repository;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import feign.Param;
import site.billingwise.api.serverapi.domain.invoice.Invoice;

public interface InvoiceRepository extends JpaRepository<Invoice, Long>, JpaSpecificationExecutor<Invoice> {
    @Query("SELECT COUNT(i) > 0 "
            + "FROM Invoice i "
            + "WHERE i.contract.id = :contractId "
            + "AND i.contractDate >= :startDate "
            + "AND i.contractDate <= :endDate")
    boolean existByMonthlyInvoice(@Param("contractId") Long contractId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    @Query("SELECT i FROM Invoice i "
            + "JOIN FETCH i.contract c "
            + "JOIN FETCH c.member m "
            + "JOIN FETCH c.item "
            + "JOIN FETCH m.client "
            + "WHERE i.id = :invoiceId")
    Optional<Invoice> findById(@Param("invoiceId") Long invoiceId);
}
