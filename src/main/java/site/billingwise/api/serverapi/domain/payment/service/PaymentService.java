package site.billingwise.api.serverapi.domain.payment.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import site.billingwise.api.serverapi.domain.payment.repository.PaymentRepository;
import site.billingwise.api.serverapi.domain.user.Client;
import site.billingwise.api.serverapi.domain.user.User;
import site.billingwise.api.serverapi.global.exception.GlobalException;
import site.billingwise.api.serverapi.global.response.info.FailureInfo;
import site.billingwise.api.serverapi.global.util.SecurityUtil;
import site.billingwise.api.serverapi.domain.payment.Payment;
import site.billingwise.api.serverapi.domain.payment.repository.PaymentAccountRepository;
import site.billingwise.api.serverapi.domain.payment.repository.PaymentCardRepository;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final PaymentAccountRepository paymentAccountRepository;
    private final PaymentCardRepository paymentCardRepository;

    // 납부 취소
    @Transactional
    public void deletePayment(Long invoiceId) {
        User user = SecurityUtil.getCurrentUser()
                .orElseThrow(() -> new GlobalException(FailureInfo.NOT_EXIST_USER));

        Payment payment = getEntity(user.getClient(), invoiceId);

        paymentRepository.delete(payment);
    }

    // 유효성 검증 후 엔티티 반환
    public Payment getEntity(Client client, Long invoiceId) {
        Payment payment = paymentRepository.findById(invoiceId)
                .orElseThrow(() -> new GlobalException(FailureInfo.NOT_EXIST_INVOICE));

        if (payment.getInvoice().getContract().getItem().getClient().getId() != client.getId()) {
            throw new GlobalException(FailureInfo.ACCESS_DENIED);
        }

        return payment;
    }

}
