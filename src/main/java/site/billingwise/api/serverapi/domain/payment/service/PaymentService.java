package site.billingwise.api.serverapi.domain.payment.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.billingwise.api.serverapi.domain.contract.PaymentType;
import site.billingwise.api.serverapi.domain.invoice.Invoice;
import site.billingwise.api.serverapi.domain.invoice.PaymentStatus;
import site.billingwise.api.serverapi.domain.invoice.repository.InvoiceRepository;
import site.billingwise.api.serverapi.domain.payment.Payment;
import site.billingwise.api.serverapi.domain.payment.PaymentMethod;
import site.billingwise.api.serverapi.domain.payment.dto.request.PayerPayAccountDto;
import site.billingwise.api.serverapi.domain.payment.dto.request.PayerPayCardDto;
import site.billingwise.api.serverapi.domain.payment.repository.PaymentCardRepository;
import site.billingwise.api.serverapi.domain.payment.repository.PaymentRepository;
import site.billingwise.api.serverapi.global.exception.GlobalException;
import site.billingwise.api.serverapi.global.feign.PayClient;
import site.billingwise.api.serverapi.global.feign.PayClientResponse;
import site.billingwise.api.serverapi.global.response.info.FailureInfo;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

    private final PayClient payClient;
    private final InvoiceRepository invoiceRepository;
    private final PaymentRepository paymentRepository;
    private final PaymentCardRepository paymentCardRepository;

    @Transactional
    public void payerPayCard(Long invoiceId, PayerPayCardDto payerPayCardDto) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                        .orElseThrow(() -> new GlobalException(FailureInfo.NOT_EXIST_INVOICE));

        if (invoice.getPaymentType() != PaymentType.PAYER_PAYMENT) {
            throw new GlobalException(FailureInfo.NOT_PAYER_PAYMENT);
        }

        if (invoice.getPaymentStatus() == PaymentStatus.PAID) {
            throw new GlobalException(FailureInfo.PAID_INVOICE);
        }

        int statusCode;

        try {
            statusCode = payClient
                    .pay(PaymentMethod.ACCOUNT.name(), payerPayCardDto.getNumber()).getStatusCode();
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            throw new GlobalException(FailureInfo.PAY_FAIL);
        }

        switch (statusCode) {
            case 200:
                break;
            case 404:
                throw new GlobalException(FailureInfo.NOT_EXIST_ACCOUNT);
            case 402:
                throw new GlobalException(FailureInfo.INSUFFICIENT_BALANCE);
            case 403:
                throw new GlobalException(FailureInfo.ACCOUNT_NOT_USABLE);
            default:
                throw new GlobalException(FailureInfo.PAY_FAIL);
        }

        Payment payment = paymentRepository.save(
                Payment.builder()
                        .id(invoiceId)
                        .invoice(invoice)
                        .paymentMethod(PaymentMethod.CARD)
                        .payAmount(invoice.getChargeAmount())
                        .build()
        );

        paymentCardRepository.save(payerPayCardDto.toEntity(payment));

    }

    public void payerPayAccount(Long invoiceId, PayerPayAccountDto payerPayAccountDto) {

    }
}
