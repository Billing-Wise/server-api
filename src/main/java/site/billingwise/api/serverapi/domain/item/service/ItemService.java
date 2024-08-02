package site.billingwise.api.serverapi.domain.item.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import site.billingwise.api.serverapi.domain.item.Item;
import site.billingwise.api.serverapi.domain.item.dto.request.CreateItemDto;
import site.billingwise.api.serverapi.domain.item.dto.request.EditItemDto;
import site.billingwise.api.serverapi.domain.item.dto.response.GetItemDto;
import site.billingwise.api.serverapi.domain.item.repository.ItemRepository;
import site.billingwise.api.serverapi.domain.user.Client;
import site.billingwise.api.serverapi.domain.user.User;
import site.billingwise.api.serverapi.global.exception.GlobalException;
import site.billingwise.api.serverapi.global.response.info.FailureInfo;
import site.billingwise.api.serverapi.global.service.S3Service;
import site.billingwise.api.serverapi.global.util.SecurityUtil;

@Service
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;
    private final S3Service s3Service;

    @Value("${aws.s3.item-directory}")
    private String itemImageDirectory;

    @Value("${aws.s3.base-image-url}")
    private String defaultImageUrl;

    @Transactional
    public GetItemDto createItem(CreateItemDto createItemDto, MultipartFile multipartFile) {
        User user = SecurityUtil.getCurrentUser().orElseThrow(() -> new GlobalException(FailureInfo.NOT_EXIST_USER));

        Item item = createItemDto.toEntity(user.getClient(), defaultImageUrl);
        itemRepository.save(item);

        if (multipartFile != null) {
            uploadImage(item, multipartFile);
        }

        return GetItemDto.toDto(item);

    }

    @Transactional
    public GetItemDto editItem(Long itemId, EditItemDto editItemDto) {
        User user = SecurityUtil.getCurrentUser().orElseThrow(
                () -> new GlobalException(FailureInfo.NOT_EXIST_USER));

        Item item = getEntity(user.getClient(), itemId);

        item.setName(editItemDto.getName());
        item.setPrice(Long.parseLong(editItemDto.getPrice()));
        item.setDescription(editItemDto.getDescription());

        return GetItemDto.toDto(item);
    }

    @Transactional
    public GetItemDto editItemImage(Long itemId, MultipartFile multipartFile) {
        User user = SecurityUtil.getCurrentUser().orElseThrow(
                () -> new GlobalException(FailureInfo.NOT_EXIST_USER));

        Item item = getEntity(user.getClient(), itemId);

        String prevImageUrl = item.getImageUrl();

        if (multipartFile == null) {
            throw new GlobalException(FailureInfo.INVALID_IMAGE);
        }

        uploadImage(item, multipartFile);
        s3Service.delete(prevImageUrl, itemImageDirectory);

        return GetItemDto.toDto(item);
    }

    @Transactional
    public void deleteItem(Long itemId) {
        User user = SecurityUtil.getCurrentUser().orElseThrow(
                () -> new GlobalException(FailureInfo.NOT_EXIST_USER));

        Item item = getEntity(user.getClient(), itemId);

        if (!item.getImageUrl().equals(defaultImageUrl)) {
            s3Service.delete(item.getImageUrl(), itemImageDirectory);
        }

        itemRepository.delete(item);
    }

    @Transactional(readOnly = true)
    public GetItemDto getItem(Long itemId) {
        User user = SecurityUtil.getCurrentUser().orElseThrow(
                () -> new GlobalException(FailureInfo.NOT_EXIST_USER));

        Item item = getEntity(user.getClient(), itemId);

        GetItemDto getItemDto = GetItemDto.toDto(item);

        return getItemDto;
    }

    @Transactional(readOnly = true)
    public Page<GetItemDto> getItemList(String itemName, Pageable pageable) {
        User user = SecurityUtil.getCurrentUser().orElseThrow(
                () -> new GlobalException(FailureInfo.NOT_EXIST_USER));

        Page<Item> itemList = null;

        if (itemName == null) {
            itemList = itemRepository.findAllByClientId(pageable, user.getClient().getId());
        } else {
            itemList = itemRepository
                    .findAllByNameContainingIgnoreCaseAndClientId(itemName, pageable, user.getClient().getId());
        }

        Page<GetItemDto> getItemDtoList = itemList.map(item -> GetItemDto.toDto(item));

        return getItemDtoList;
    }

    private void uploadImage(Item item, MultipartFile multipartFile) {

        if (!multipartFile.getContentType().startsWith("image/")) {
            throw new GlobalException(FailureInfo.INVALID_IMAGE);
        }

        String imageUrl = s3Service.upload(multipartFile, itemImageDirectory);
        item.setImageUrl(imageUrl);

    }

    public Item getEntity(Client client, Long itemId) {
        Item item = itemRepository.findById(itemId).orElseThrow(
                () -> new GlobalException(FailureInfo.ITEM_NOT_FOUND));

        if (item.getClient().getId() != client.getId()) {
            throw new GlobalException(FailureInfo.ACCESS_DENIED);
        }

        return item;
    }

}
