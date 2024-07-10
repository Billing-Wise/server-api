package site.billingwise.api.serverapi.domain.member.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import site.billingwise.api.serverapi.domain.member.Member;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findById(Long id);

    boolean existsByEmail(String email);
}
