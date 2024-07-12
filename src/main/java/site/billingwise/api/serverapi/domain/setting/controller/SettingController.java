package site.billingwise.api.serverapi.domain.setting.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import site.billingwise.api.serverapi.domain.setting.dto.request.SetBasicItemsDto;
import site.billingwise.api.serverapi.domain.setting.service.SettingService;
import site.billingwise.api.serverapi.global.response.BaseResponse;
import site.billingwise.api.serverapi.global.response.info.SuccessInfo;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/setting")
public class SettingController {

    private final SettingService settingService;

    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/easy-consent/items")
    public BaseResponse setBasicItems(@Valid @RequestBody SetBasicItemsDto setBasicItemsDto) {
        settingService.setBasicItems(setBasicItemsDto);
        return new BaseResponse(SuccessInfo.SET_ITEMS_TO_BASIC);
    }

    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/easy-consent/payments")
    public BaseResponse setBasisPayments(@Valid @RequestBody SetBasicItemsDto setBasicItemsDto) {
        settingService.setBasicItems(setBasicItemsDto);
        return new BaseResponse(SuccessInfo.SET_ITEMS_TO_BASIC);
    }
}
