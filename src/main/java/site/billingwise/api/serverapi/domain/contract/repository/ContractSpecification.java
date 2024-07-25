package site.billingwise.api.serverapi.domain.contract.repository;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.Predicate;
import site.billingwise.api.serverapi.domain.contract.Contract;
import site.billingwise.api.serverapi.domain.contract.ContractStatus;
import site.billingwise.api.serverapi.domain.contract.PaymentType;
import site.billingwise.api.serverapi.domain.invoice.InvoiceType;
import site.billingwise.api.serverapi.global.util.EnumUtil;

public class ContractSpecification {
    public static Specification<Contract> findContract(
            Long itemId,
            Long MemberId,
            String itemName,
            String memberName,
            Boolean isSubscription,
            Long invoiceTypeId,
            Long contractStatusId,
            Long paymentTypeId,
            Long clientId) {
        return ((root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (itemId != null) {
                predicates.add(criteriaBuilder.equal(root.get("item").get("id"), itemId));
            }

            if (MemberId != null) {
                predicates.add(criteriaBuilder.equal(root.get("member").get("id"), MemberId));
            }

            if (itemName != null && !itemName.isEmpty()) {
                predicates.add(criteriaBuilder.like(root.get("item").get("name"), "%" + itemName + "%"));
            }

            if (memberName != null && !memberName.isEmpty()) {
                predicates.add(criteriaBuilder.like(root.get("member").get("name"), "%" + memberName + "%"));
            }

            if (isSubscription != null) {
                predicates.add(criteriaBuilder.equal(root.get("isSubscription"), isSubscription));
            }

            if (invoiceTypeId != null) {
                predicates.add(criteriaBuilder.equal(root.get("invoiceType"), 
                    EnumUtil.toEnum(InvoiceType.class, invoiceTypeId)));
            }

            if (contractStatusId != null) {
                predicates.add(criteriaBuilder.equal(root.get("contractStatus"), 
                    EnumUtil.toEnum(ContractStatus.class, contractStatusId)));
            }

            if (paymentTypeId != null) {
                predicates.add(criteriaBuilder.equal(root.get("paymentType"), 
                    EnumUtil.toEnum(PaymentType.class, paymentTypeId)));
            }

            if (clientId != null) {
                predicates.add(criteriaBuilder.equal(root
                        .get("member")
                        .get("client")
                        .get("id"), clientId));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        });
    }
}
