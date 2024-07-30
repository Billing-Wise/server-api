package site.billingwise.api.serverapi.domain.invoice.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import site.billingwise.api.serverapi.domain.invoice.dto.request.CreateInvoiceDto;
import site.billingwise.api.serverapi.domain.invoice.dto.request.EditInvoiceDto;
import site.billingwise.api.serverapi.domain.invoice.dto.response.GetInvoiceDto;
import site.billingwise.api.serverapi.domain.invoice.dto.response.GetInvoiceListDto;
import site.billingwise.api.serverapi.domain.invoice.service.InvoiceService;
import site.billingwise.api.serverapi.global.response.BaseResponse;
import site.billingwise.api.serverapi.global.response.DataResponse;
import site.billingwise.api.serverapi.global.response.info.SuccessInfo;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/invoices")
public class InvoiceController {

    private final InvoiceService invoiceService;

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{invoiceId}/send")
    public BaseResponse sendInvoice(@PathVariable("invoiceId") Long invoiceId) {
        invoiceService.sendInvoice(invoiceId);

        return new BaseResponse(SuccessInfo.INVOICE_SENDED);
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping()
    public BaseResponse createInvoice(@Valid @RequestBody CreateInvoiceDto createInvoiceDto) {
        invoiceService.createInvoice(createInvoiceDto);

        return new BaseResponse(SuccessInfo.INVOICE_CREATED);
    }

    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/{invoiceId}")
    public DataResponse<GetInvoiceDto> editInvoice(@PathVariable("invoiceId") Long invoiceId,
            @Valid @RequestBody EditInvoiceDto editInvoiceDto) {
        GetInvoiceDto getInvoiceDto = invoiceService.editInvoice(invoiceId, editInvoiceDto);

        return new DataResponse<>(SuccessInfo.INVOICE_UPDATED, getInvoiceDto);
    }

    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping("/{invoiceId}")
    public BaseResponse deleteInvoice(@PathVariable("invoiceId") Long invoiceId) {
        invoiceService.deleteInvoice(invoiceId);

        return new BaseResponse(SuccessInfo.INVOICE_DELETED);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{invoiceId}")
    public DataResponse<GetInvoiceDto> getInvoice(@PathVariable("invoiceId") Long invoiceId) {
        GetInvoiceDto getInvoiceDto = invoiceService.getInvoice(invoiceId);

        return new DataResponse<>(SuccessInfo.INVOICE_LOADED, getInvoiceDto);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping()
    public DataResponse<Page<GetInvoiceListDto>> getInvoiceList(
            @RequestParam(name = "contractId", required = false) Long contractId,
            @RequestParam(name = "itemName", required = false) String itemName,
            @RequestParam(name = "memberName", required = false) String memberName,
            @RequestParam(name = "paymentStatusId", required = false) Long paymentStatusId,
            @RequestParam(name = "paymentTypeId", required = false) Long paymentTypeId,
            @RequestParam(name = "startContractDate", required = false) LocalDate startContractDate,
            @RequestParam(name = "endContractDate", required = false) LocalDate endContractDate,
            @RequestParam(name = "startDueDate", required = false) LocalDate startDueDate,
            @RequestParam(name = "endDueDate", required = false) LocalDate endDueDate,
            @RequestParam(name = "startCreatedAt", required = false) LocalDate startCreatedAt,
            @RequestParam(name = "endCreatedAt", required = false) LocalDate endCreatedAt,
            Pageable pageable) {
        Page<GetInvoiceListDto> getInvoiceList = invoiceService.getInvoiceList(
                contractId, itemName, memberName, paymentStatusId, paymentTypeId, startContractDate, endContractDate,
                startDueDate, endDueDate, startCreatedAt, endCreatedAt, pageable);

        return new DataResponse<>(SuccessInfo.INVOICE_LOADED, getInvoiceList);
    }

}