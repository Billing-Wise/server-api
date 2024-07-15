package site.billingwise.api.serverapi.domain.consent.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import site.billingwise.api.serverapi.domain.consent.dto.response.GetBasicItemDto;
import site.billingwise.api.serverapi.domain.consent.service.EasyConsentService;
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
}
