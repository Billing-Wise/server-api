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
import site.billingwise.api.serverapi.domain.payment.PaymentAccount;
import site.billingwise.api.serverapi.domain.payment.PaymentCard;
import site.billingwise.api.serverapi.domain.payment.repository.PaymentAccountRepository;
import site.billingwise.api.serverapi.domain.payment.repository.PaymentCardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import site.billingwise.api.serverapi.domain.contract.PaymentType;
import site.billingwise.api.serverapi.domain.invoice.Invoice;
import site.billingwise.api.serverapi.domain.invoice.PaymentStatus;
import site.billingwise.api.serverapi.domain.invoice.repository.InvoiceRepository;
import site.billingwise.api.serverapi.domain.payment.PaymentMethod;
import site.billingwise.api.serverapi.domain.payment.dto.request.PayerPayAccountDto;
import site.billingwise.api.serverapi.domain.payment.dto.request.PayerPayCardDto;
import site.billingwise.api.serverapi.domain.payment.repository.PaymentRepository;
import site.billingwise.api.serverapi.global.exception.GlobalException;
import site.billingwise.api.serverapi.global.feign.PayClient;
import site.billingwise.api.serverapi.global.feign.PayClientResponse;
import site.billingwise.api.serverapi.global.response.info.FailureInfo;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final PaymentAccountRepository paymentAccountRepository;
    private final PaymentCardRepository paymentCardRepository;

    private final PayClient payClient;

    public void payerPayCard(Long invoiceId, PayerPayCardDto payerPayCardDto) {
        int statusCode = payClient
                .pay(PaymentMethod.ACCOUNT.name(), payerPayCardDto.getNumber()).getStatusCode();

    }

    public void payerPayAccount(Long invoiceId, PayerPayAccountDto payerPayAccountDto) {

    }

    // 납부 취소
    @Transactional
    public void deletePayment(Long invoiceId) {
        User user = SecurityUtil.getCurrentUser()
                .orElseThrow(() -> new GlobalException(FailureInfo.NOT_EXIST_USER));

        Payment payment = getEntity(user.getClient(), invoiceId);

        if (payment.getPaymentMethod().equals(PaymentMethod.CARD)) {
            PaymentCard paymentCard = paymentCardRepository.findById(invoiceId)
                    .orElseThrow(() -> new GlobalException(FailureInfo.NOT_EXIST_PAYMENT_CARD));
            paymentCardRepository.delete(paymentCard);
        } else if (payment.getPaymentMethod().equals(PaymentMethod.ACCOUNT)) {
            PaymentAccount paymentAccount = paymentAccountRepository.findById(invoiceId)
                    .orElseThrow(() -> new GlobalException(FailureInfo.NOT_EXIST_PAYMENT_ACCOUNT));
            paymentAccountRepository.delete(paymentAccount);
        }

        paymentRepository.delete(payment);
    }

    // 유효성 검증 후 엔티티 반환
    public Payment getEntity(Client client, Long invoiceId) {
        Payment payment = paymentRepository.findById(invoiceId)
                .orElseThrow(() -> new GlobalException(FailureInfo.NOT_EXIST_PAYMENT));

        if (payment.getInvoice().getContract().getItem().getClient().getId() != client.getId()) {
            throw new GlobalException(FailureInfo.ACCESS_DENIED);
        }

        return payment;
    }

}
