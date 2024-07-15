package site.billingwise.api.serverapi.domain.consent.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import site.billingwise.api.serverapi.domain.consent.dto.request.RegisterConsentDto;
import site.billingwise.api.serverapi.domain.consent.dto.response.GetConsentDto;
import site.billingwise.api.serverapi.domain.consent.service.ConsentService;
import site.billingwise.api.serverapi.global.response.BaseResponse;
import site.billingwise.api.serverapi.global.response.DataResponse;
import site.billingwise.api.serverapi.global.response.info.SuccessInfo;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/consents")
public class ConsentController {

    private final ConsentService consentService;

    @PostMapping("/{memberId}")
    @ResponseStatus(HttpStatus.OK)
    public BaseResponse registerConsent(
            @PathVariable Long memberId,
            @Valid @RequestPart(name = "data") RegisterConsentDto registerConsentDto,
            @RequestPart(name = "signImage") MultipartFile multipartFile) {
        consentService.registerConsent(memberId, registerConsentDto, multipartFile);
        return new BaseResponse(SuccessInfo.CONSENT_REGISTERED);
    }

    @GetMapping("/{memberId}")
    @ResponseStatus(HttpStatus.OK)
    public DataResponse<GetConsentDto> getConsent(@PathVariable Long memberId) {
        return new DataResponse<>(SuccessInfo.GET_CONSENT,
                consentService.getConsent(memberId));
    }

    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/{memberId}")
    public BaseResponse editConsent(@PathVariable Long memberId,
                                    @Valid @RequestBody RegisterConsentDto editConsentDto) {
        consentService.editConsent(memberId, editConsentDto);
        return new BaseResponse(SuccessInfo.CONSENT_EDITED);
    }

    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/{memberId}/image")
    public BaseResponse editItemImage(@PathVariable Long memberId,
                                      @RequestPart(name = "signImage") MultipartFile multipartFile) {
        consentService.editConsentSignImage(memberId, multipartFile);
        return new BaseResponse(SuccessInfo.CONSENT_SIGN_IMAGE_EDITED);
    }

    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping("/{memberId}")
    public BaseResponse deleteConsent(@PathVariable Long memberId) {
        consentService.deleteConsent(memberId);
        return new BaseResponse(SuccessInfo.CONSENT_DELETED);
    }
}
