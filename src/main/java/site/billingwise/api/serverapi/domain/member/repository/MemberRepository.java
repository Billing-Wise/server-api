package site.billingwise.api.serverapi.domain.member.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import site.billingwise.api.serverapi.domain.member.Member;

public interface MemberRepository extends JpaRepository<Member, Long>, JpaSpecificationExecutor<Member> {

    Optional<Member> findById(Long id);

    boolean existsByEmail(String email);

    @Query("SELECT m FROM Member m "
            + "LEFT JOIN FETCH m.contractList c "
            + "LEFT JOIN FETCH c.invoiceList i "
            + "WHERE m.id = :memberId")
    Optional<Member> findByIdWithContractsWithInvoices(@Param("memberId") Long memberId);

    @Query("SELECT m FROM Member m "
            + "WHERE m.client.id = :clientId")
    Page<Member> findByClientId(@Param("clientId") Long clientId, Pageable pageable);

    @Query("SELECT m FROM Member m "
            + "WHERE m.client.id = :clientId AND m.name LIKE %:memberName%")
    Page<Member> findByClientIdAndName(@Param("clientId") Long clientId,
                                       @Param("memberName") String memberName, Pageable pageable);

    boolean existsByClientIdAndEmail(Long clientId, String memberEmail);
}
