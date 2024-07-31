package site.billingwise.api.serverapi.domain.invoice.repository;

import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;
import java.time.LocalTime;

import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.Predicate;
import site.billingwise.api.serverapi.domain.contract.PaymentType;
import site.billingwise.api.serverapi.domain.invoice.Invoice;
import site.billingwise.api.serverapi.domain.invoice.PaymentStatus;
import site.billingwise.api.serverapi.global.util.EnumUtil;

public class InvoiceSpecification {
    public static Specification<Invoice> findContract(
            Long contractId,
            String itemName,
            String memberName,
            Long paymentStatusId,
            Long paymentTypeId,
            LocalDate startContractDate,
            LocalDate endContractDate,
            LocalDate startDueDate,
            LocalDate endDueDate,
            LocalDate startCreatedAt,
            LocalDate endCreatedAt,
            Long clientId) {
        return ((root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (clientId != null) {
                predicates.add(criteriaBuilder.equal(root
                        .get("contract")
                        .get("member")
                        .get("client")
                        .get("id"), clientId));
            }

            if (contractId != null) {
                predicates.add(criteriaBuilder.equal(root.get("contract").get("id"), contractId));
            }

            if (itemName != null) {
                predicates
                        .add(criteriaBuilder.like(root.get("contract").get("item").get("name"), "%" + itemName + "%"));
            }

            if (memberName != null) {
                predicates.add(
                        criteriaBuilder.like(root.get("contract").get("member").get("name"), "%" + memberName + "%"));
            }

            if (paymentStatusId != null) {
                predicates.add(criteriaBuilder.equal(root.get("paymentStatus"),
                        EnumUtil.toEnum(PaymentStatus.class, paymentStatusId)));
            }

            if (paymentTypeId != null) {
                predicates.add(criteriaBuilder.equal(root.get("paymentType"),
                        EnumUtil.toEnum(PaymentType.class, paymentTypeId)));
            }

            if (startContractDate != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("contractDate"),
                        startContractDate.atStartOfDay()));
            }

            if (endContractDate != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("contractDate"),
                        endContractDate.atTime(LocalTime.MAX)));
            }

            if (startDueDate != null) {
                predicates.add(
                        criteriaBuilder.greaterThanOrEqualTo(root.get("dueDate"), startDueDate.atStartOfDay()));
            }

            if (endDueDate != null) {
                predicates
                        .add(criteriaBuilder.lessThanOrEqualTo(root.get("dueDate"), endDueDate.atTime(LocalTime.MAX)));
            }

            if (startCreatedAt != null) {
                predicates.add(
                        criteriaBuilder.greaterThanOrEqualTo(root.get("createdAt"), startCreatedAt.atStartOfDay()));
            }

            if (endCreatedAt != null) {
                predicates.add(
                        criteriaBuilder.lessThanOrEqualTo(root.get("createdAt"), endCreatedAt.atTime(LocalTime.MAX)));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        });
    }
}
