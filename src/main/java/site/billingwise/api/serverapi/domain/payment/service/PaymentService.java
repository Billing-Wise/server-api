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
import lombok.extern.slf4j.Slf4j;
import site.billingwise.api.serverapi.domain.contract.PaymentType;
import site.billingwise.api.serverapi.domain.invoice.Invoice;
import site.billingwise.api.serverapi.domain.invoice.PaymentStatus;
import site.billingwise.api.serverapi.domain.invoice.repository.InvoiceRepository;
import site.billingwise.api.serverapi.domain.payment.PaymentMethod;
import site.billingwise.api.serverapi.domain.payment.dto.response.GetPaymentDto;
import site.billingwise.api.serverapi.domain.payment.dto.response.GetPaymentAccountDto;
import site.billingwise.api.serverapi.domain.payment.dto.response.GetPaymentCardDto;
import site.billingwise.api.serverapi.domain.payment.dto.request.PayerPayAccountDto;
import site.billingwise.api.serverapi.domain.payment.dto.request.PayerPayCardDto;
import site.billingwise.api.serverapi.domain.payment.dto.response.GetPayerPayInvoiceDto;
import site.billingwise.api.serverapi.global.feign.PayClient;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {
    private final PayClient payClient;

    private final InvoiceRepository invoiceRepository;
    private final PaymentRepository paymentRepository;
    private final PaymentAccountRepository paymentAccountRepository;
    private final PaymentCardRepository paymentCardRepository;

    @Transactional
    public void payerPayCard(Long invoiceId, PayerPayCardDto payerPayCardDto) {
        Invoice invoice = checkInvoiceValidation(invoiceId);

        int statusCode = requestPay(PaymentMethod.CARD, payerPayCardDto.getNumber());

        switch (statusCode) {
            case 200:
                break;
            case 404:
                throw new GlobalException(FailureInfo.NOT_EXIST_CARD);
            case 402:
                throw new GlobalException(FailureInfo.INSUFFICIENT_BALANCE);
            case 403:
                throw new GlobalException(FailureInfo.CARD_NOT_USABLE);
            default:
                throw new GlobalException(FailureInfo.PAY_FAIL);
        }

        Payment payment = savePayment(invoice, PaymentMethod.CARD);

        paymentCardRepository.save(payerPayCardDto.toEntity(payment));

        invoice.setPaymentStatus(PaymentStatus.PAID);
    }

    @Transactional
    public void payerPayAccount(Long invoiceId, PayerPayAccountDto payerPayAccountDto) {
        Invoice invoice = checkInvoiceValidation(invoiceId);

        int statusCode = requestPay(PaymentMethod.ACCOUNT, payerPayAccountDto.getNumber());

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

        Payment payment = savePayment(invoice, PaymentMethod.ACCOUNT);

        paymentAccountRepository.save(payerPayAccountDto.toEntity(payment));

        invoice.setPaymentStatus(PaymentStatus.PAID);
    }

    public GetPayerPayInvoiceDto getPayerPayInvoice(Long invoiceId) {
        return GetPayerPayInvoiceDto.toDto(checkInvoiceValidation(invoiceId));
    }

    private Invoice checkInvoiceValidation(Long invoiceId) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new GlobalException(FailureInfo.NOT_EXIST_INVOICE));

        if (invoice.getPaymentType() != PaymentType.PAYER_PAYMENT) {
            throw new GlobalException(FailureInfo.NOT_PAYER_PAYMENT);
        }

        if (invoice.getPaymentStatus() == PaymentStatus.PAID) {
            throw new GlobalException(FailureInfo.PAID_INVOICE);
        }

        return invoice;
    }

    private int requestPay(PaymentMethod paymentMethod, String number) {
        try {
            return payClient.pay(paymentMethod.name(), number).getStatusCode();
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            throw new GlobalException(FailureInfo.PAY_FAIL);
        }
    }

    private Payment savePayment(Invoice invoice, PaymentMethod paymentMethod) {
        return paymentRepository.save(
                Payment.builder()
                        .id(invoice.getId())
                        .invoice(invoice)
                        .paymentMethod(paymentMethod)
                        .payAmount(invoice.getChargeAmount())
                        .build());
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

    // 납부 조회
    @Transactional(readOnly = true)
    public GetPaymentDto getPayment(Long invoiceId) {
        User user = SecurityUtil.getCurrentUser()
                .orElseThrow(() -> new GlobalException(FailureInfo.NOT_EXIST_USER));

        Payment payment = getEntity(user.getClient(), invoiceId);

        if (payment.getPaymentMethod().equals(PaymentMethod.ACCOUNT)) {
            PaymentAccount paymentAccount = paymentAccountRepository.findById(invoiceId)
                    .orElseThrow(() -> new GlobalException(FailureInfo.NOT_EXIST_PAYMENT_ACCOUNT));
            
            GetPaymentDto getPaymentDto = GetPaymentAccountDto.builder()
                        .invoiceId(invoiceId)
                        .payAmount(payment.getPayAmount())
                        .paymentMethod(payment.getPaymentMethod())
                        .createAt(payment.getCreatedAt())
                        .number(paymentAccount.getNumber())
                        .bank(paymentAccount.getBank())
                        .owner(paymentAccount.getOwner())
                        .build();

            return getPaymentDto;

        } else if (payment.getPaymentMethod().equals(PaymentMethod.CARD)) {
            PaymentCard paymentCard = paymentCardRepository.findById(invoiceId)
                    .orElseThrow(() -> new GlobalException(FailureInfo.NOT_EXIST_PAYMENT_CARD));

            GetPaymentDto getPaymentDto = GetPaymentCardDto.builder()
                    .invoiceId(invoiceId)
                    .payAmount(payment.getPayAmount())
                    .paymentMethod(payment.getPaymentMethod())
                    .createAt(payment.getCreatedAt())
                    .number(paymentCard.getNumber())
                    .company(paymentCard.getCompany())
                    .owner(paymentCard.getOwner())
                    .build();

            return getPaymentDto;      
        }
        
        throw new GlobalException(FailureInfo.INVALID_INPUT);
    }

    // 유효성 검증 후 납부 엔티티 반환
    public Payment getEntity(Client client, Long invoiceId) {
        Payment payment = paymentRepository.findById(invoiceId)
                .orElseThrow(() -> new GlobalException(FailureInfo.NOT_EXIST_PAYMENT));

        if (payment.getInvoice().getContract().getItem().getClient().getId() != client.getId()) {
            throw new GlobalException(FailureInfo.ACCESS_DENIED);
        }

        return payment;
    }

}
