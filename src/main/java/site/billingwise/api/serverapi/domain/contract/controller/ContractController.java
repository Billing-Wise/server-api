package site.billingwise.api.serverapi.domain.contract.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import site.billingwise.api.serverapi.domain.contract.dto.request.CreateContractDto;
import site.billingwise.api.serverapi.domain.contract.dto.request.EditContractDto;
import site.billingwise.api.serverapi.domain.contract.dto.response.CreateBulkContractResultDto;
import site.billingwise.api.serverapi.domain.contract.dto.response.GetContractAllDto;
import site.billingwise.api.serverapi.domain.contract.dto.response.GetContractDto;
import site.billingwise.api.serverapi.domain.contract.service.ContractService;
import site.billingwise.api.serverapi.global.response.BaseResponse;
import site.billingwise.api.serverapi.global.response.DataResponse;
import site.billingwise.api.serverapi.global.response.info.FailureInfo;
import site.billingwise.api.serverapi.global.response.info.SuccessInfo;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PutMapping;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/contracts")
public class ContractController {
    private final ContractService contractService;

    @ResponseStatus(HttpStatus.OK)
    @PostMapping()
    public DataResponse<Long> createContract(@Valid @RequestBody CreateContractDto createContractDto) {
        Long contractId = contractService.createContract(createContractDto);

        return new DataResponse<>(SuccessInfo.CONTRACT_CREATED, contractId);
    }

    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/{contractId}")
    public BaseResponse editContract(@PathVariable("contractId") Long contractId,
            @Valid @RequestBody EditContractDto editContractDto) {
        contractService.editContract(contractId, editContractDto);

        return new BaseResponse(SuccessInfo.CONTRACT_EDITED);
    }

    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping("/{contractId}")
    public BaseResponse deleteContract(@PathVariable("contractId") Long contractId) {
        contractService.deleteContract(contractId);

        return new BaseResponse(SuccessInfo.CONTRACT_EDITED);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{contractId}")
    public DataResponse<GetContractDto> getContract(@PathVariable("contractId") Long contractId) {
        GetContractDto getContractDto = contractService.getContract(contractId);

        return new DataResponse<>(SuccessInfo.CONTRACT_LOADED, getContractDto);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping()
    public DataResponse<Page<GetContractAllDto>> getContractList(
            @RequestParam(name = "itemId", required = false) Long itemId,
            @RequestParam(name = "memberId", required = false) Long memberId,
            @RequestParam(name = "itemName", required = false) String itemName,
            @RequestParam(name = "memberName", required = false) String memberName,
            @RequestParam(name = "isSubscription", required = false) Boolean isSubscription,
            @RequestParam(name = "invoiceTypeId", required = false) Long invoiceTypeId,
            @RequestParam(name = "contractStatusId", required = false) Long contractStatusId,
            @RequestParam(name = "paymentTypeId", required = false) Long paymentTypeId,
            Pageable pageable) {
        Page<GetContractAllDto> getContractList = contractService.getContractList(
                itemId, memberId, itemName, memberName, isSubscription, invoiceTypeId, contractStatusId, paymentTypeId,
                pageable);

        return new DataResponse<>(SuccessInfo.CONTRACT_LOADED, getContractList);
    }

    @PostMapping("/bulk-register")
    public DataResponse<?> createContractBulk(@RequestPart("file") MultipartFile file) {
        CreateBulkContractResultDto createBulkContractResultDto = contractService.createContractBulk(file);
        if (createBulkContractResultDto.isSuccess()) {
            List<CreateContractDto> createContractDtoList = createBulkContractResultDto.getContractList();
            return new DataResponse<>(SuccessInfo.CONTRACT_CREATED, createContractDtoList);
        } else {
            List<String> errorList = createBulkContractResultDto.getErrorList();
            return new DataResponse<>(FailureInfo.INVALID_FILE, errorList);
        }
    }

}
