package site.billingwise.api.serverapi.domain.member.repository;

import java.util.List;
import java.util.ArrayList;

import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.Predicate;
import site.billingwise.api.serverapi.domain.member.Member;

public class MemberSpecification {
    public static Specification<Member> findMember(
            String name,
            String email,
            String phone,
            Long clientId) {
        return ((root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (name != null && !name.isEmpty()) {
                predicates.add(criteriaBuilder.like(root.get("name"), "%" + name + "%"));
            }

            if (email != null && !email.isEmpty()) {
                predicates.add(criteriaBuilder.like(root.get("email"), "%" + email + "%"));
            }

            if (phone != null && !phone.isEmpty()) {
                predicates.add(criteriaBuilder.like(root.get("phone"), "%" + phone + "%"));
            }

            if (clientId != null) {
                predicates.add(criteriaBuilder.equal(root
                        .get("client")
                        .get("id"), clientId));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        });
    }
}
