package site.billingwise.api.serverapi.domain.member.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import site.billingwise.api.serverapi.domain.member.Member;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findById(Long id);

    boolean existsByEmail(String email);

    @Query("SELECT m FROM Member m " +
        "LEFT JOIN FETCH m.contractList c " +
        "LEFT JOIN FETCH c.invoiceList i " +
        "LEFT JOIN FETCH i.paymentStatus p " +
        "WHERE m.id = :memberId")
    Optional<Member> findByIdWithContractsWithInvoicesWithPaymentStatus(@Param("memberId") Long memberId);
}
