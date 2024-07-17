package site.billingwise.api.serverapi.domain.payment.service;

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

    private final PayClient payClient;
    private final InvoiceRepository invoiceRepository;
    private final PaymentRepository paymentRepository;

    public void payerPayCard(Long invoiceId, PayerPayCardDto payerPayCardDto) {
//        Invoice invoice = invoiceRepository.findById(invoiceId)
//                        .orElseThrow(() -> new GlobalException(FailureInfo.NOT_EXIST_INVOICE));
//
//        if (invoice.getPaymentStatus() == PaymentStatus.PAID) {
//            throw new GlobalException(FailureInfo.PAID_INVOICE);
//        }

        int statusCode = payClient
                .pay(PaymentMethod.ACCOUNT.name(), payerPayCardDto.getNumber()).getStatusCode();





    }

    public void payerPayAccount(Long invoiceId, PayerPayAccountDto payerPayAccountDto) {

    }
}
