package site.billingwise.api.serverapi.domain.invoice.service;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import site.billingwise.api.serverapi.domain.contract.Contract;
import site.billingwise.api.serverapi.domain.contract.service.ContractService;
import site.billingwise.api.serverapi.domain.invoice.dto.request.CreateInvoiceDto;
import site.billingwise.api.serverapi.domain.invoice.repository.InvoiceRepository;
import site.billingwise.api.serverapi.domain.user.User;
import site.billingwise.api.serverapi.global.exception.GlobalException;
import site.billingwise.api.serverapi.global.response.info.FailureInfo;
import site.billingwise.api.serverapi.global.util.SecurityUtil;

@Service
@RequiredArgsConstructor
public class InvoiceService {

    private final ContractService contractService;
    private final InvoiceRepository invoiceRepository;
    

    public void createInvoice(CreateInvoiceDto createInvoiceDto) {
        User user = SecurityUtil.getCurrentUser()
            .orElseThrow(() -> new GlobalException(FailureInfo.NOT_EXIST_USER));

        Contract contract = contractService.getEntity(user.getClient(), createInvoiceDto.getContractId());

        
    }
}
