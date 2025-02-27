package site.billingwise.api.serverapi.domain.payment.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import site.billingwise.api.serverapi.domain.payment.dto.response.GetPaymentDto;
import site.billingwise.api.serverapi.domain.payment.dto.request.PayerPayAccountDto;
import site.billingwise.api.serverapi.domain.payment.dto.request.PayerPayCardDto;
import site.billingwise.api.serverapi.domain.payment.dto.response.GetPayerPayInvoiceDto;
import site.billingwise.api.serverapi.domain.payment.service.PaymentService;
import site.billingwise.api.serverapi.global.response.BaseResponse;
import site.billingwise.api.serverapi.global.response.DataResponse;
import site.billingwise.api.serverapi.global.response.info.SuccessInfo;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/payments")
public class PaymentController {

    private final PaymentService paymentService;

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/payer-pay/invoices/{invoiceId}")
    public DataResponse<GetPayerPayInvoiceDto> getPayerPayInvoice(@PathVariable Long invoiceId) {
        return new DataResponse(SuccessInfo.GET_PAYER_PAY_INVOICE,
                paymentService.getPayerPayInvoice(invoiceId));
    }
  
    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/payer-pay/card")
    public BaseResponse payerPayCard(Long invoiceId,
                                     @Valid @RequestBody PayerPayCardDto payerPayCardDto) {
        paymentService.payerPayCard(invoiceId, payerPayCardDto);
        return new BaseResponse(SuccessInfo.PAYER_PAY_CARD);

    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/payer-pay/account")
    public BaseResponse payerPayAccount(Long invoiceId,
                                        @Valid @RequestBody PayerPayAccountDto payerPayAccountDto) {
        paymentService.payerPayAccount(invoiceId, payerPayAccountDto);
        return new BaseResponse(SuccessInfo.PAYER_PAY_ACCOUNT);
    }

    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping("/{invoiceId}")
    public BaseResponse deletePayment(@PathVariable("invoiceId") Long invoiceId) {
        paymentService.deletePayment(invoiceId);
        return new BaseResponse(SuccessInfo.PAYMENT_DELETED);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{invoiceId}")
    public DataResponse<GetPaymentDto> getPayment(@PathVariable("invoiceId") Long invoiceId) {
        GetPaymentDto getPaymentDto = paymentService.getPayment(invoiceId);
        return new DataResponse<>(SuccessInfo.PAYMENT_LOADED, getPaymentDto);
    }
}
