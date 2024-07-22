package site.billingwise.api.serverapi.domain.item.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import site.billingwise.api.serverapi.domain.item.dto.request.CreateItemDto;
import site.billingwise.api.serverapi.domain.item.dto.request.EditItemDto;
import site.billingwise.api.serverapi.domain.item.dto.response.GetItemDto;
import site.billingwise.api.serverapi.domain.item.service.ItemService;
import site.billingwise.api.serverapi.global.response.BaseResponse;
import site.billingwise.api.serverapi.global.response.DataResponse;
import site.billingwise.api.serverapi.global.response.info.SuccessInfo;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/items")
public class ItemController {

    private final ItemService itemService;

    @ResponseStatus(HttpStatus.OK)
    @PostMapping()
    public DataResponse<GetItemDto> createItem(@Valid @RequestPart(name = "data") CreateItemDto createItemDto,
            @RequestPart(name = "image", required = false) MultipartFile multipartFile) {
                GetItemDto getItemDto = itemService.createItem(createItemDto, multipartFile);

        return new DataResponse<>(SuccessInfo.ITEM_CREATED, getItemDto);
    }

    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/{itemId}")
    public BaseResponse editItem(@PathVariable("itemId") Long itemId, @Valid @RequestBody EditItemDto editItemDto) {
        itemService.editItem(itemId, editItemDto);

        return new BaseResponse(SuccessInfo.ITEM_EDITED);
    }

    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/{itemId}/image")
    public BaseResponse editItemImage(@PathVariable("itemId") Long itemId,
            @RequestPart(name = "image", required = false) MultipartFile multipartFile) {
        itemService.editItemImage(itemId, multipartFile);

        return new BaseResponse(SuccessInfo.ITEM_IMAGE_EDITED);
    }

    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping("/{itemId}")
    public BaseResponse deleteItem(@PathVariable("itemId") Long itemId) {
        itemService.deleteItem(itemId);

        return new BaseResponse(SuccessInfo.ITEM_DELETED);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{itemId}")
    public DataResponse<GetItemDto> getItem(@PathVariable("itemId") Long itemId) {

        GetItemDto getItemDto = itemService.getItem(itemId);

        return new DataResponse<>(SuccessInfo.ITEM_LOADED, getItemDto);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping()
    public DataResponse<Page<GetItemDto>> getItemList(@RequestParam(name = "name", required = false) String itemName,
            Pageable pageable) {

        Page<GetItemDto> getItemDtoList = itemService.getItemList(itemName, pageable);

        return new DataResponse<>(SuccessInfo.ITEM_LOADED, getItemDtoList);
    }

}
