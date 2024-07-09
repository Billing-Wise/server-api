package site.billingwise.api.serverapi.domain.item.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.Valid;
import jakarta.websocket.server.PathParam;
import lombok.RequiredArgsConstructor;
import site.billingwise.api.serverapi.domain.item.dto.request.CreateItemDto;
import site.billingwise.api.serverapi.domain.item.service.ItemService;
import site.billingwise.api.serverapi.global.response.BaseResponse;
import site.billingwise.api.serverapi.global.response.info.SuccessInfo;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/items")
public class ItemController {

    private final ItemService itemService;

    @ResponseStatus(HttpStatus.OK)
    @PostMapping()
    public BaseResponse createItem(@Valid @RequestPart(name = "data") CreateItemDto writePostRequestDto,
            @RequestPart(name = "image", required = false) MultipartFile multipartFile) {

        itemService.createItem(writePostRequestDto, multipartFile);

        return new BaseResponse(SuccessInfo.ITEM_CREATED);
    }

    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/{itemId}/image")
    public BaseResponse editItemImage(@PathVariable Long itemId,
            @RequestPart(name = "image", required = false) MultipartFile multipartFile) {

        itemService.editItemImage(itemId, multipartFile);

        return new BaseResponse(SuccessInfo.ITEM_IMAGE_EDITED);
    }

}
