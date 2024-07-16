package site.billingwise.api.serverapi.domain.consent.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import site.billingwise.api.serverapi.domain.consent.dto.request.ConsentWithNonMemberDto;
import site.billingwise.api.serverapi.domain.consent.dto.response.GetBasicItemDto;
import site.billingwise.api.serverapi.domain.consent.dto.response.GetContractInfoDto;
import site.billingwise.api.serverapi.domain.consent.service.EasyConsentService;
import site.billingwise.api.serverapi.global.response.BaseResponse;
import site.billingwise.api.serverapi.global.response.DataResponse;
import site.billingwise.api.serverapi.global.response.info.SuccessInfo;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/easy-consent")
public class EasyConsentController {

    private final EasyConsentService easyConsentService;

    @GetMapping("/non-member/items")
    @ResponseStatus(HttpStatus.OK)
    public DataResponse<List<GetBasicItemDto>> getBasicItemList(Long clientId) {
        return new DataResponse<>(SuccessInfo.GET_BASIC_ITEM_LIST,
                easyConsentService.getBasicItemList(clientId));
    }

    @PostMapping("/non-member")
    @ResponseStatus(HttpStatus.OK)
    public BaseResponse consentWithNonMember(
            Long clientId,
            @Valid @RequestPart(name = "data") ConsentWithNonMemberDto consentWithNonMemberDto,
            @RequestPart(name = "signImage") MultipartFile multipartFile) {
        easyConsentService.consentWithNonMember(clientId, consentWithNonMemberDto, multipartFile);
        return new BaseResponse(SuccessInfo.CONSENT_WITH_NON_MEMBER);
    }

    @GetMapping("/member/contracts/{contractId}")
    @ResponseStatus(HttpStatus.OK)
    public DataResponse<GetContractInfoDto> getContractInfo(@PathVariable Long contractId) {
        return new DataResponse<>(SuccessInfo.GET_EASY_CONSENT_CONTRACT_INFO,
                easyConsentService.getContractInfo(contractId));
    }
}
